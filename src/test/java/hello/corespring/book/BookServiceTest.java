package hello.corespring.book;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
class BookServiceTest {

    @Mock
    BookRepository bookRepository; //가짜객체

    @Test
    public void save() {
        Book book = new Book();

        when(bookRepository.save(book)).thenReturn(book);   //save라는 메소드 호출될 때 book 이 들어오면 book을 리턴해.
      //  BookRepository bookRepository = new BookRepository();
        BookService bookService = new BookService(bookRepository);

        Book result = bookService.save(book);

        assertThat(book.getCreated()).isNotNull();
        assertThat(book.getBookStatus()).isEqualTo(BookStatus.DRAFT);
        assertThat(result).isNotNull();

    }
}