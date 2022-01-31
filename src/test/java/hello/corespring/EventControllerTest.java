package hello.corespring;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
//WebMvcTest 어노테이션은 슬라이싱 테스트라고 해서, 부트와 관련된건데, 계층형 테스트임. 웹과 관련된 빈만 등록을 해주는. 주로 컨트롤러만 등록이 돼. 그러니까 컨버터와 포매터가 제대로 빈으로 등록 안되면 테스트가 깨질 우려가 있음.
@WebMvcTest({EventConverter.StringToEventConverter.class, EventController.class}) //이런 경우에 이런식으로 빈으로 등록을 하고 테스트를 할 수 있음.
class EventControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    public void getTest() throws Exception {
        mockMvc.perform(get("/event/1"))    //요청을 보내면
                .andExpect(status().isOk()) //응답이 200으로 나와서 문제가 없어야 하고
                .andExpect(content().string("1"));  //결과가 1이 나와야 해
    }

}