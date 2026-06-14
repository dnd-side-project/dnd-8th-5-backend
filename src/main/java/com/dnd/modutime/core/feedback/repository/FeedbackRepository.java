package com.dnd.modutime.core.feedback.repository;

import com.dnd.modutime.core.feedback.domain.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}
