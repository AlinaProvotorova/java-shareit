package ru.practicum.shareit.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.annotation.Rollback;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@DisplayName("Тесты класса CommentDto")
public class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> jacksonTesterCommentDto;
    @Autowired
    private JacksonTester<CommentResponseDto> jacksonTesterCommentResponseDto;


    @Test
    @DisplayName("Тест на сериализацию класса CommentDto")
    @SneakyThrows
    @Rollback(true)
    void commentDtoTest() {

        User user = User.builder()
                .id(1L)
                .name("Игорь")
                .email("Super@yandex.ru")
                .build();

        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("Всё хорошо")
                .author(user)
                .build();

        JsonContent<CommentDto> commentDtoJsonContent = jacksonTesterCommentDto.write(commentDto);

        assertThat(commentDtoJsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(commentDtoJsonContent).extractingJsonPathStringValue("$.text").isEqualTo("Всё хорошо");
        assertThat(commentDtoJsonContent).extractingJsonPathNumberValue("$.author.id").isEqualTo(1);
        assertThat(commentDtoJsonContent).extractingJsonPathStringValue("$.author.name").isEqualTo("Игорь");
        assertThat(commentDtoJsonContent).extractingJsonPathStringValue("$.author.email").isEqualTo("Super@yandex.ru");
    }

    @Test
    @DisplayName("Тест на десериализацию класса CommentDto")
    @SneakyThrows
    @Rollback(true)
    void commentDtoReadTest() {
        String comment
                = "{\"id\":1,\"text\":\"Всё хорошо\",\"author\":{\"id\":1,\"name\":\"Игорь\",\"email\":\"Super@yandex.ru\"}}";
        CommentDto commentDto = jacksonTesterCommentDto.parseObject(comment);

        assertThat(1L).isEqualTo(commentDto.getId());
        assertThat("Всё хорошо").isEqualTo(commentDto.getText());
        assertThat(1L).isEqualTo(commentDto.getAuthor().getId());
        assertThat("Игорь").isEqualTo(commentDto.getAuthor().getName());
        assertThat("Super@yandex.ru").isEqualTo(commentDto.getAuthor().getEmail());
    }

    @Test
    @DisplayName("Тест на сериализацию класса CommentResponseDto")
    @SneakyThrows
    @Rollback(true)
    void commentResponseDtoTest() {

        CommentResponseDto commentResponseDto = CommentResponseDto.builder()
                .id(1L)
                .text("Всё хорошо")
                .authorName("Иван")
                .created(LocalDateTime.of(2023, 7, 7, 12, 12))
                .build();

        JsonContent<CommentResponseDto> commentResponseDtoJsonContent = jacksonTesterCommentResponseDto.write(commentResponseDto);

        assertThat(commentResponseDtoJsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(commentResponseDtoJsonContent).extractingJsonPathStringValue("$.text").isEqualTo("Всё хорошо");
        assertThat(commentResponseDtoJsonContent).extractingJsonPathStringValue("$.authorName").isEqualTo("Иван");
    }

    @Test
    @DisplayName("Тест на десериализацию класса CommentResponseDto")
    @SneakyThrows
    @Rollback(true)
    void commentResponseDtoReadTest() {
        String comment
                = "{\"id\":1,\"text\":\"Всё хорошо\",\"authorName\":\"Иван\",\"created\":\"2023-07-07T12:12\"}";
        CommentResponseDto commentResponseDto = jacksonTesterCommentResponseDto.parseObject(comment);

        assertThat(1L).isEqualTo(commentResponseDto.getId());
        assertThat("Всё хорошо").isEqualTo(commentResponseDto.getText());
        assertThat("Иван").isEqualTo(commentResponseDto.getAuthorName());
        assertThat("2023-07-07T12:12").isEqualTo(commentResponseDto.getCreated().toString());


    }


}