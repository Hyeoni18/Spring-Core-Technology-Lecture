package hello.corespring;

import java.beans.PropertyEditorSupport;

public class EventEditor extends PropertyEditorSupport {
    //implement PropertyEditor 를 직접 구현해도 되지만, 메소드가 많아서 PropertyEditorSupport 를 상속받음. 그러면 구현하려는 메소드만 선택해서 구현 가능.
    //보통 이 2개를 많이 구현함. getAsText, setAsText
    //우리에게 필요한 건 텍스트를 이벤트로 변환하는 것. 그래서 사실 setAsText 만 구현해도 됨.
    @Override
    public String getAsText() {
        Event evnet = (Event) getValue(); //프로퍼티 에디터가 받은 객체를 getValue로 가져올 수 있음. 근데 이걸로 가져오면 Object 타입으로 나옴. 그래서 타입을 변환시켜줌
        return evnet.getId().toString();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(new Event(Integer.parseInt(text))); //생성자에 전달, setValue 에 넣어준다.
    }
    //여기서 getValue, setValue 에서 공유하는 값이 프로퍼티 에디터가 가지고 있는 값이야. 이 값이 서로 다른 쓰레드에게 공유가 되는데. 얘는 스테이트 풀이야. 상태를 저장하고 있음. 쓰레드 세이프하지 않음. 그래서 이 프로퍼티 에디터에 구현체들은 여러 쓰레드에 공유해서 사용하면 안됨. 즉, 얘를 빈으로 등록해서 쓰면 안된다는거야. 1번이 2번 정보를 수정하고 5번이 1번 정보를 수정하는 경우가 생길 수도 있어. 절대로 프로퍼티 에디터를 빈으로 등록해서 쓰면 안돼. 쓰레드 스코프. 의 빈으로 만들어서 쓰는건 괜찮아. 프로토 타입, 싱글톤 타입으로 얘기했던 부분. 추가로 한 쓰레드 내에서만 유효한 그런 스코프의 빈도 있음. 그렇게만 정의해서 쓰면 그나마 괜찮지만. 아예 안하는걸 추천함. 다른 쓰레드랑 공유해서 쓰면 안돼.
    //그럼 어떻게 이 프로퍼티 에디터를 사용해?
}
