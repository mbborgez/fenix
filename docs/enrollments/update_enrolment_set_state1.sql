-- SELECT CONCAT('UPDATE ENROLMENT SET STATE = 1 WHERE ID_INTERNAL = ', E.ID_INTERNAL, ';') AS ""
-- FROM ENROLMENT_EVALUATION EE
-- INNER JOIN ENROLMENT E ON EE.KEY_ENROLMENT = E.ID_INTERNAL
-- INNER JOIN CURRICULAR_COURSE CC ON E.KEY_CURRICULAR_COURSE = CC.ID_INTERNAL
-- INNER JOIN STUDENT_CURRICULAR_PLAN SCP ON E.KEY_STUDENT_CURRICULAR_PLAN = SCP.ID_INTERNAL
-- WHERE 
-- (EE.GRADE IS NOT NULL AND EE.GRADE <> 'RE') AND 
-- E.STATE = 3;

SELECT CONCAT('UPDATE ENROLMENT SET STATE = 1 WHERE ID_INTERNAL = ', E.ID_INTERNAL, ';') AS ""
FROM ENROLMENT E
INNER JOIN ENROLMENT_EVALUATION EE ON EE.KEY_ENROLMENT = E.ID_INTERNAL
WHERE
EE.GRADE IS NOT NULL AND
EE.GRADE <> 'NA' AND
EE.GRADE <> 'RE' AND
E.STATE <> 1;
