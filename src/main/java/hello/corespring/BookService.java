package hello.corespring;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class BookService implements InitializingBean {

    @Autowired
    BookRepository myBookRepository;

    public void printBookRepository() {
        System.out.println(myBookRepository.getClass());
    }

    @PostConstruct
    public void setUp() {
        //빈이 만들어진 다음에 해야할 일
        System.out.println(myBookRepository.getClass());
    }

    //InitializingBean 인터페이스를 구현하면 필요한 메소드
    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
