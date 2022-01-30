package hello.corespring.book;

import java.util.Date;

//bean 은 아니야, 스프링 IOC 컨테이너가 관리하는 객체가 아니기 때문이야. 그냥 java bean 이라고는 할 수 있을거야.
public class Book {

    private Date created;

    private BookStatus bookStatus;

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public BookStatus getBookStatus() {
        return bookStatus;
    }

    public void setBookStatus(BookStatus bookStatus) {
        this.bookStatus = bookStatus;
    }

}
