package hello.corespring;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class EventValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Event.class.equals(clazz); //Event 클래스 타입의 인스턴스를 검증할거야. 파라미터로 넘어오는 클래스의 타입이 이벤트인지 확인하고.
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "notempty", "Empty title is now allowed");
        //errors 에다가, 이벤트 title 이 그렇다면, errorCode 로 notempty 라고 줄거야, 디폴트로는 Empty title is now allowed 사용할거야.
    }

}
