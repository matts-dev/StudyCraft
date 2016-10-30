package StudyCraft;

/**
 * Class that provides quiz functionality using internal states.
 * 
 * @author Matt Stone
 * @version 5/20/2016
 *
 */
public class QuizState {
	protected StringBuilder strStateOfQuiz = new StringBuilder();
	protected StudyModule baseModule = null;
	protected int quizIter;
	

	public QuizState(StudyModule baseModule) {
		this.baseModule = baseModule;
	}
	
	public void startLinearQuiz(){
		
	}
	
	public String getNextString(){
		return null;
	}
}
