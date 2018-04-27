package com.i000.stock.user.dao.service;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.i000.stock.user.core.constant.enums.ApplicationErrorMessage;
import com.i000.stock.user.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.ParameterizedType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional
public abstract class AbstractService<T, M extends BaseMapper<T>> extends ServiceImpl<M, T> {

    protected static final int ZERO = 0;

    @Autowired
    private M mapper;

    private Class<T> modelClass;

    @SuppressWarnings({"unchecked"})
    protected AbstractService() {
        this.modelClass = (Class<T>) ((ParameterizedType) (getClass().getGenericSuperclass()))
                .getActualTypeArguments()[0];
    }

    @Transactional(readOnly = true)
    public T get(Long id) {
        return baseMapper.selectById(id);
    }

    /**
     * 获取 `is_deleted = 0` 的数据总数
     *
     * @return 总数
     */
    @Transactional(readOnly = true)
    public Integer count() {
        return baseMapper.selectCount(createEntityWrapper());
    }

    /**
     * 根据主键校验数据是否存在, 默认用 <code>ApplicationErrorMessage.NOT_EXISTS</code> 的错误提示信息
     *
     * @param id 模型主键
     */
    @Transactional(readOnly = true)
    public void checkModelExistsById(Long id) {
        checkModelExistsById(id, ApplicationErrorMessage.NOT_EXISTS.getMsg());
    }

    /**
     * 根据主键校验数据是否存在, 自定义错误信息
     *
     * @param id      主键
     * @param message 错误信息
     */
    @Transactional(readOnly = true)
    public void checkModelExistsById(Long id, String message) {
        if (!isExistsById(id)) {
            throw new ServiceException(
                    ApplicationErrorMessage.NOT_EXISTS.getCode(),
                    message
            );
        }
    }


    /**
     * 是否存在
     *
     * @param id 主键
     * @return true | false
     */
    @Transactional(readOnly = true)
    public boolean isExistsById(Long id) {
        EntityWrapper<T> entityWrapper = createEntityWrapper();
        entityWrapper.eq("id", id);

        Integer count = baseMapper.selectCount(entityWrapper);

        return count > ZERO;
    }

    /**
     * 创建 entity wrapper 对象
     *
     * @return EntityWrapper<T>
     */
    protected EntityWrapper<T> createEntityWrapper() {
        return new EntityWrapper<>();
    }

    /**
     * 获取mapper
     *
     * @return mapper
     */
    protected M getMapper() {
        return this.mapper;
    }

    /**
     * 移出第一个元素, 若集合为null或元素为空,则返回 null
     *
     * @param entities 模型集
     * @return null or entity
     */
    protected T peek(List<T> entities) {
        return Optional
                .ofNullable(entities)
                .filter(CollectionUtils::isNotEmpty)
                .map(e -> e.get(0))
                .orElse(null);
    }

}