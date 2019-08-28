package cn.bucheng.consumer.handle;


import cn.bucheng.consumer.constant.BusinessErrorEnum;
import cn.bucheng.consumer.exception.BusinessException;
import cn.bucheng.consumer.model.ServerModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.List;

/**
 * @author ：yinchong
 * @create ：2019/7/25 17:32
 * @description：
 * @modified By：
 * @version:
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {Exception.class, Throwable.class})
    @ResponseBody
    public Object handleError(HttpServletRequest req, HttpServletResponse resp, Object error) {
        log.error(error.toString());
        if (error instanceof BusinessException) {
            BusinessException bus = (BusinessException) error;
            return ServerModel.fail(bus.getCode(), bus.getMessage());
        } else if (error instanceof ServletRequestBindingException) {
            return ServerModel.fail(BusinessErrorEnum.BIND_PARAM_FAIL.getCode(), BusinessErrorEnum.BIND_PARAM_FAIL.getMessage());
        } else if (error instanceof NoHandlerFoundException) {
            return ServerModel.fail(BusinessErrorEnum.NO_FIND_ROUTING.getCode(), BusinessErrorEnum.NO_FIND_ROUTING.getMessage());
        } else if (error instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException mse = (MethodArgumentNotValidException) error;
            return ServerModel.fail(BusinessErrorEnum.PARAM_VERIFY_FAIL.getCode(), BusinessErrorEnum.PARAM_VERIFY_FAIL.getMessage() + ":" + errorMessage(mse.getBindingResult()));
        } else {
            if (error instanceof RuntimeException) {
                RuntimeException err = (RuntimeException) error;
                log.error(printStackTraceError(err.getStackTrace()));
            }
            return ServerModel.error("服务器异常", error.toString());
        }
    }


    private List<String> errorMessage(BindingResult result) {
        List<String> errors = new LinkedList<>();
        List<ObjectError> allErrors = result.getAllErrors();
        if (allErrors != null) {
            for (ObjectError error : allErrors) {
                errors.add(error.getDefaultMessage());
            }
        }

        return errors;
    }

    private String printStackTraceError(StackTraceElement[] stacks) {
        if (stacks == null || stacks.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        for (StackTraceElement stack : stacks) {
            sb.append(stack.toString()).append("\n");
        }
        return sb.toString();
    }
}
