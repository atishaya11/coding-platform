package com.dscjss.codingplatform.submissions;

import com.dscjss.codingplatform.submissions.dto.SubmissionDto;
import com.dscjss.codingplatform.submissions.dto.SubmissionRequest;
import com.dscjss.codingplatform.submissions.exception.InvalidSubmissionException;
import com.dscjss.codingplatform.submissions.exception.SubmissionFailedException;
import com.dscjss.codingplatform.users.dto.UserBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SubmissionService {


    int submit(SubmissionRequest submissionRequest) throws InvalidSubmissionException, SubmissionFailedException;

    SubmissionDto getSubmission(UserBean userBean, Integer submissionId, boolean onlySummary);

    Page<SubmissionDto> getSubmissions(UserBean userBean, String code, Pageable pageable);
}
