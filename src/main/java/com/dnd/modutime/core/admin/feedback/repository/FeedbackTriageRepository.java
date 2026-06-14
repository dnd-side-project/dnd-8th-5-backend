package com.dnd.modutime.core.admin.feedback.repository;

import com.dnd.modutime.core.admin.feedback.domain.FeedbackTriage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FeedbackTriageRepository extends JpaRepository<FeedbackTriage, Long> {

    Optional<FeedbackTriage> findByFeedbackId(Long feedbackId);

    List<FeedbackTriage> findByFeedbackIdIn(Collection<Long> feedbackIds);
}
