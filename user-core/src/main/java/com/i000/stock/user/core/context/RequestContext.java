package com.i000.stock.user.core.context;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 13:46 2018/5/3
 * @Modified By:
 */
public class RequestContext {
    private static final ThreadLocal<RequestContext> REQUEST_CONTEXT_THREAD_LOCAL = new ThreadLocal<>();

    private String sign;
    private String amountShare;
    private String accountCode;

    private RequestContext(RequestContextBuild requestContextBuild) {
        this.sign = requestContextBuild.sign;
        this.accountCode = requestContextBuild.accountCode;
        this.amountShare = requestContextBuild.amountShare;
        setContext(this);
    }

    public String getSign() {
        return sign;
    }

    public String getAmountShare() {
        return amountShare;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public static RequestContext getInstance() {
        return REQUEST_CONTEXT_THREAD_LOCAL.get();
    }

    public static void clean() {
        REQUEST_CONTEXT_THREAD_LOCAL.remove();
    }

    private void setContext(RequestContext context) {
        REQUEST_CONTEXT_THREAD_LOCAL.set(context);
    }

    public static class RequestContextBuild {

        public String sign;
        public String amountShare;
        public String accountCode;

        public RequestContextBuild sign(String sign) {
            this.sign = sign;
            return this;
        }

        public RequestContextBuild amountShare(String amountShare) {
            this.amountShare = amountShare;
            return this;
        }

        public RequestContextBuild accountCode(String accountCode) {
            this.accountCode = accountCode;
            return this;
        }


        public RequestContext build() {
            return new RequestContext(this);
        }

    }
}
