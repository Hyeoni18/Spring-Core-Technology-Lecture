package hello.corespring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component @Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS) //프록시를 사용하지 않는 다는 옵션이 디폴ㅇ트임.
public class Proto {

//    @Autowired
//    Single single;
}
