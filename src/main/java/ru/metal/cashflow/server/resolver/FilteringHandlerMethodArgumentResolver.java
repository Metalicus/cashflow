package ru.metal.cashflow.server.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import ru.metal.cashflow.server.request.FilterRequest;

import java.util.Map;

/**
 * Extracts filtering information from request
 * <p>
 * Ignores page, size and order parameters, other will be added to Filtering object
 * <p>
 * Method can return {@code null} if is no filtering
 */
public class FilteringHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    public static final String DEFAULT_PAGE_PARAMETER = "page";
    public static final String DEFAULT_SIZE_PARAMETER = "size";
    public static final String DEFAULT_ORDER_PARAMETER = "sort";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return FilterRequest.class.equals(parameter.getParameterType());
    }

    @Override
    public FilterRequest resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        final Map<String, String[]> parameterMap = webRequest.getParameterMap();
        if (parameterMap.keySet().isEmpty())
            return null;

        final FilterRequest filterRequest = new FilterRequest();
        for (String key : parameterMap.keySet()) {
            if (DEFAULT_PAGE_PARAMETER.equals(key) || DEFAULT_SIZE_PARAMETER.equals(key) || DEFAULT_ORDER_PARAMETER.equals(key))
                continue;

            filterRequest.addFilter(new FilterRequest.Filter(key, webRequest.getParameter(key)));
        }

        return filterRequest.isEmpty() ? null : filterRequest;
    }
}
