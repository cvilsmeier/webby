package org.webby;

/**
 * Classes implementing this interface must have a constructor that has a <code>AppContext</code> argument.
 * Example:
 * <pre>
 * public class MyApp implements App {
 *   
 *   public MyApp(AppContext appcontext) throws Exception {
 *   }
 *   
 *   ...
 * }
 * </pre>
 */
public interface App {

	void serve(WebRequest req) throws Exception;
	
	void destroy();

}
