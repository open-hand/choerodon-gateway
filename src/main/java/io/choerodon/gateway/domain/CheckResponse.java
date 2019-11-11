package io.choerodon.gateway.domain;

public class CheckResponse {

    private String jwt;

    private String message;

    private CheckState status;

    private String routeRuleCode;

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CheckState getStatus() {
        return status;
    }

    public void setStatus(CheckState status) {
        this.status = status;
    }

    public String getRouteRuleCode() {
        return routeRuleCode;
    }

    public void setRouteRuleCode(String routeRuleCode) {
        this.routeRuleCode = routeRuleCode;
    }

    @Override
    public String toString() {
        return "CheckResponse{" +
                "jwt='" + jwt + '\'' +
                ", message='" + message + '\'' +
                ", status=" + status +
                ", routeRuleCode='" + routeRuleCode + '\'' +
                '}';
    }
}
