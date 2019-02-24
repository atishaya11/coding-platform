
DROP PROCEDURE IF EXISTS update_scores;
DELIMITER $$
CREATE PROCEDURE update_scores (p_contest_id INT)
BEGIN

  UPDATE contest_registered_user AS users INNER JOIN
    (SELECT SUM(temp.max_score) AS total_score, user_id, contest_id  FROM
      (SELECT contest_id, user_id, MAX(sr.score) AS max_score FROM submission AS s JOIN submission_result AS sr ON
          s.result_id = sr.id WHERE s.contest_id = p_contest_id AND sr.status = 5 GROUP BY s.user_id, s.problem_id) AS temp GROUP BY user_id) AS scores ON
        users.contest_id = scores.contest_id
  SET users.score = scores.total_score WHERE users.user_id = scores.user_id;

END;
DELIMITER ;


DROP PROCEDURE IF EXISTS update_cli_submissions_scores;

DELIMITER $$
CREATE PROCEDURE update_cli_submissions_scores (p_contest_id INT, p_problem_id INT)
BEGIN
  DECLARE min_length INT;
  DECLARE max_length INT;
  DECLARE ratio DOUBLE;

  SELECT MIN(sr.source_char_count) INTO min_length FROM submission AS s JOIN submission_result AS sr ON s.result_id = sr.id
  WHERE s.contest_id = p_contest_id AND sr.status = 5 AND s.problem_id = p_problem_id;
  SELECT MAX(sr.source_char_count) INTO max_length FROM submission AS s JOIN submission_result AS sr ON s.result_id = sr.id
  WHERE s.contest_id = p_contest_id AND sr.status = 5 AND s.problem_id = p_problem_id;


  UPDATE submission_result AS sr INNER JOIN (SELECT * FROM submission AS t WHERE t.contest_id = p_contest_id) AS s ON sr.id = s.result_id SET sr.score = 20
  WHERE sr.source_char_count = max_length AND sr.status = 5 AND s.problem_id = p_problem_id ;

  UPDATE submission_result AS sr INNER JOIN (SELECT * FROM submission AS t WHERE t.contest_id = p_contest_id) AS s ON sr.id = s.result_id SET sr.score = 100
  WHERE sr.source_char_count = min_length AND sr.status = 5 AND s.problem_id = p_problem_id ;


  IF max_length - min_length > 0 THEN
    SET ratio = 80 / (max_length - min_length);
    UPDATE submission_result AS sr INNER JOIN submission AS s ON sr.id = s.result_id SET sr.score = 100 - (ratio * (sr.source_char_count - min_length))
    WHERE s.contest_id = p_contest_id AND sr.status = 5 AND s.problem_id = p_problem_id ;

  END IF;

END;
DELIMITER ;

DROP PROCEDURE IF EXISTS update_scores;

DELIMITER $$
CREATE PROCEDURE update_scores (p_contest_id INT)
BEGIN

  UPDATE contest_registered_user AS users INNER JOIN
    (SELECT * , (@rn := @rn+1) AS position FROM (SELECT SUM(temp.max_score) AS total_score, user_id, contest_id   FROM
      (SELECT contest_id, user_id, MAX(sr.score) AS max_score FROM submission AS s JOIN submission_result AS sr ON
          s.result_id = sr.id WHERE s.contest_id = p_contest_id AND sr.status = 5 GROUP BY s.user_id, s.problem_id) AS temp
           GROUP BY user_id ORDER BY total_score DESC)AS scores CROSS JOIN (SELECT @rn := 0) AS params) AS scores ON
        users.contest_id = scores.contest_id
  SET users.score = scores.total_score, users.position = scores.position  WHERE users.user_id = scores.user_id;

END $$
DELIMITER ;