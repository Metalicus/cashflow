package ru.metal.cashflow.server.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import ru.metal.cashflow.server.request.ReportRequest;

import java.util.Map;

/**
 * Extracts report additional information from request
 * <p>
 * Method can return {@code null} if is no additional parameters
 */
public class ReportHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return ReportRequest.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        final Map<String, String[]> parameterMap = webRequest.getParameterMap();
        if (parameterMap.keySet().isEmpty())
            return null;

        final ReportRequest reportRequest = new ReportRequest();
        for (String key : parameterMap.keySet()) {
            reportRequest.addParameter(key, webRequest.getParameter(key));
        }

        return reportRequest.isEmpty() ? null : reportRequest;
    }
}
