package com.dscjss.codingplatform.submissions;

import com.dscjss.codingplatform.contests.exception.NotFoundException;
import com.dscjss.codingplatform.error.InvalidRequestException;
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

    void updateSubmission(int id);

    Page<SubmissionDto> getSubmissions(UserBean userBean, String contest, String problem, Pageable pageable, boolean b);

    Page<SubmissionDto> getSubmissionsByUser(UserBean userBean, String code, Pageable pageable, String user);

    Page<SubmissionDto> getSubmissionsByUser(UserBean userBean, String contest, String code, Pageable pageable, String user);
}
