package com.dscjss.codingplatform.util;

import com.dscjss.codingplatform.submissions.dto.SubmissionRequest;
import com.dscjss.codingplatform.users.dto.UserBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Utility {

    public static Pageable createPageable(int page, String sortBy, String order, int pageSize) {
        if(page < 1)
            page = 1;
        page -= 1;
        return PageRequest.of(page, pageSize,
                Sort.by( order != null && order.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
                        sortBy != null ? sortBy : "id"));
    }

    public static SubmissionRequest createSubmissionRequest(UserBean userBean, MultipartFile multipartFile, int problemId, int compilerId, int contestProblemId) throws IOException {

        SubmissionRequest submissionRequest = new SubmissionRequest();

        submissionRequest.setUserBean(userBean);
        submissionRequest.setCompilerId(compilerId);
        submissionRequest.setProblemId(problemId);
        submissionRequest.setContestProblemId(contestProblemId);
        submissionRequest.setSource(getString(multipartFile));

        return submissionRequest;
    }

    private static String getString(MultipartFile multipartFile) throws IOException {
        return new String(multipartFile.getBytes(), StandardCharsets.UTF_8);
    }


}
