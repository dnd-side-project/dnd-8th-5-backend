package com.dnd.modutime.core.feedback.application;

import com.dnd.modutime.core.feedback.application.command.FeedbackAuthor;
import com.dnd.modutime.core.feedback.application.command.FeedbackCreateCommand;
import com.dnd.modutime.core.feedback.domain.Feedback;
import com.dnd.modutime.core.feedback.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    @Transactional
    public Long create(FeedbackCreateCommand command) {
        FeedbackAuthor author = command.author();
        Feedback feedback = new Feedback(
                command.category(),
                command.content(),
                command.replyEmail(),
                command.interviewAgreed(),
                command.interviewPhoneNumber(),
                command.responses(),
                command.snapshot(),
                author.type(),
                author.userId(),
                author.name(),
                author.email()
        );
        return feedbackRepository.save(feedback).getId();
    }
}
