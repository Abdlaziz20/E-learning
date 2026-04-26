package com.miniProjectApp.metier;

import  com.miniProjectApp.dao.*;
import  com.miniProjectApp.entity.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Business Logic Layer
 * All application logic goes here.
 * Presentation calls Metier → Metier calls DAO → DAO hits DB.
 */
public class ElearningService {

    private final UserDAO     userDAO     = new UserDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final QuizDAO     quizDAO     = new QuizDAO();
    private final QuestionDAO questionDAO = new QuestionDAO();
    private final ResultDAO   resultDAO   = new ResultDAO();

    // ── AUTHENTICATION ────────────────────────────────────────────────────

    /**
     * Login – returns User if credentials match, null otherwise.
     */
    public User login(String email, String password) throws SQLException {
        if (email == null || email.isEmpty() || password == null || password.isEmpty())
            return null;
        return userDAO.findByEmailAndPassword(email.trim().toLowerCase(), password);
    }

    /**
     * Register a new student or teacher.
     */
    public User register(String fullName, String email, String password, String role) throws Exception {
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty())
            throw new Exception("All fields are required.");
        if (password.length() < 6)
            throw new Exception("Password must be at least 6 characters.");
        if (userDAO.emailExists(email.trim().toLowerCase()))
            throw new Exception("This email is already registered.");

        User u;
        if ("TEACHER".equalsIgnoreCase(role)) {
            u = new Teacher(fullName, email.trim().toLowerCase(), password);
        } else {
            u = new Student(fullName, email.trim().toLowerCase(), password);
        }
        // Assign a random avatar color
        String[] colors = {"#3B82F6","#8B5CF6","#10B981","#F59E0B","#EF4444","#EC4899"};
        u.setAvatarColor(colors[(int)(Math.random() * colors.length)]);
        userDAO.insert(u);
        return u;
    }

    // ── CATEGORIES ────────────────────────────────────────────────────────

    public List<Category> getAllCategories() throws SQLException {
        return categoryDAO.findAll();
    }

    // ── TEACHERS ─────────────────────────────────────────────────────────

    public List<User> getAllTeachers() throws SQLException {
        return userDAO.findTeachers();
    }

    // ── QUIZ MANAGEMENT (Teacher) ─────────────────────────────────────────

    /**
     * Create a new quiz (step 1 – teacher fills quiz info).
     * Returns the new quiz with its generated ID.
     */
    public Quiz createQuiz(String title, String description, int teacherId,
                           int categoryId, int timeLimit) throws Exception {
        if (title.isEmpty()) throw new Exception("Quiz title is required.");
        if (categoryId <= 0) throw new Exception("Please select a category.");

        Quiz q = new Quiz();
        q.setTitle(title);
        q.setDescription(description);
        q.setTeacherId(teacherId);
        q.setCategoryId(categoryId);
        q.setTimeLimit(timeLimit);
        quizDAO.insert(q);
        return q;
    }

    /**
     * Add one question to an existing quiz (step 2 – repeated for each question).
     */
    public void addQuestion(int quizId, String questionText,
                            String optA, String optB, String optC, String optD,
                            String correctOption, int points) throws Exception {
        if (questionText.isEmpty()) throw new Exception("Question text is required.");
        if (optA.isEmpty() || optB.isEmpty() || optC.isEmpty() || optD.isEmpty())
            throw new Exception("All four options are required.");
        if (!correctOption.matches("[ABCD]"))
            throw new Exception("Correct option must be A, B, C, or D.");

        int orderNum = questionDAO.countByQuiz(quizId) + 1;

        Question q = new Question();
        q.setQuizId(quizId);
        q.setQuestionText(questionText);
        q.setOptionA(optA);
        q.setOptionB(optB);
        q.setOptionC(optC);
        q.setOptionD(optD);
        q.setCorrectOption(correctOption.toUpperCase());
        q.setPoints(points);
        q.setOrderNum(orderNum);
        questionDAO.insert(q);
    }

    /** Get all quizzes created by a teacher */
    public List<Quiz> getTeacherQuizzes(int teacherId) throws SQLException {
        return quizDAO.findByTeacher(teacherId);
    }

    /** Get questions of a quiz */
    public List<Question> getQuizQuestions(int quizId) throws SQLException {
        return questionDAO.findByQuiz(quizId);
    }

    /** Delete a quiz (cascades to questions) */
    public void deleteQuiz(int quizId) throws SQLException {
        quizDAO.delete(quizId);
    }

    // ── QUIZ BROWSING (Student) ────────────────────────────────────────────

    /** Filter quizzes by teacher and/or category */
    public List<Quiz> filterQuizzes(int teacherId, int categoryId) throws SQLException {
        if (teacherId > 0 && categoryId > 0)
            return quizDAO.findByTeacherAndCategory(teacherId, categoryId);
        if (teacherId > 0)
            return quizDAO.findByTeacher(teacherId);
        if (categoryId > 0)
            return quizDAO.findByCategory(categoryId);
        return quizDAO.findAll();
    }

    // ── SCORING ────────────────────────────────────────────────────────────

    /**
     * Grade the student's answers and save the result.
     *
     * @param studentId    current student's user id
     * @param quiz         the quiz being graded
     * @param questions    ordered list of questions
     * @param answers      student's answers: answers[i] = "A"/"B"/"C"/"D" or null
     * @param timeTakenSec how many seconds the student took
     * @return  the persisted Result object
     */
    public Result gradeQuiz(int studentId, Quiz quiz, List<Question> questions,
                            String[] answers, int timeTakenSec) throws SQLException {

        int correct = 0, totalPoints = 0;
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            totalPoints += q.getPoints();
            if (i < answers.length && answers[i] != null
                    && answers[i].equalsIgnoreCase(q.getCorrectOption())) {
                correct++;
            }
        }

        // Score = weighted percentage of correct answers
        int score = questions.isEmpty() ? 0 : (correct * 100) / questions.size();

        Result r = new Result();
        r.setStudentId(studentId);
        r.setQuizId(quiz.getId());
        r.setScore(score);
        r.setTotalQuestions(questions.size());
        r.setCorrectAnswers(correct);
        r.setTimeTaken(timeTakenSec);
        resultDAO.insert(r);

        // Hydrate for display
        r.setQuizTitle(quiz.getTitle());
        return r;
    }

    // ── RESULTS ────────────────────────────────────────────────────────────

    public List<Result> getStudentResults(int studentId) throws SQLException {
        return resultDAO.findByStudent(studentId);
    }

    public List<Result> getQuizLeaderboard(int quizId) throws SQLException {
        return resultDAO.findByQuiz(quizId);
    }
}
