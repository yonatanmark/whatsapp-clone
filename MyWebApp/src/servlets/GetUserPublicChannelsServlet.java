package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import servletClasses.Channel;

/**
 * This servlet is used to get all public channels belonging to a given username
 * 
 * @author YONATAN
 * @since 01-03-2017
 * 
 **/
@WebServlet({ "/GetPublicChannels", "/GetPublicChannels/*", "/GetPublicChannels/userName/*" })
public class GetUserPublicChannelsServlet extends ChatmeServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public GetUserPublicChannelsServlet() {
		super();
	}

	/**
	 * doGet method to get the public channels of a given username
	 * 
	 * @param request
	 * 			the request from the client side
	 * 		  response
	 * 			the response to contact the client side
	 * @throws ServletException, IOException
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//getting the username field
		String c = "userName";
		String usernameToShow = getRequestDataFromUrl(request, response, c);

		System.out.println("in doGet in viewPublicChannelsServlet user name = " + usernameToShow);

		// declaring the connection and the statement objects to use
		Connection connection = null;
		Statement statement = null;
		try {
			// getting the connection and the statement objects
			// connection = LogInServlet.getUserDatabase(request, response);
			connection = super.getDatabase(request, response);
			// PreparedStatement pstatement = null;// = connection.();

			// getting the top topics
			Channel[] privateChannels = getUserPrivateChannel(response, connection, usernameToShow);

			// sending the finishing of the JSON formatted string to the client.
			Gson gson = new Gson();
			String dataToSend = gson.toJson(privateChannels) /* + " }" */;

			super.sendData(response, dataToSend);

		} // handling the exceptions
		catch (IOException exception) {
			System.err.println("uzilizing DB error #2");
			exception.printStackTrace();
		} // releasing the resources
		finally {

			try {

				if (statement != null)
					statement.close();

				if (connection != null)
					connection.close();

			}

			catch (SQLException exception) {

				System.err.println("can't access to DB");
				exception.printStackTrace();

			}

		}
	}

	/**
	 * Method to get array of public channel objects from the username.
	 * 
	 * @param request
	 * 			the request from the client side.
	 * 		  response
	 * 			the response to contact the client side.
	 * 		  requestingUsername
	 * 			the username of the client.
	 * @returns array of private channels corresponding to the user.
	 * 			
	 */
	private Channel[] getUserPrivateChannel(HttpServletResponse response, Connection connection,
			String requestingUsername) {

		String sql = "SELECT * FROM USERCHANNELS INNER JOIN CHANNELS ON "
				+ " USERCHANNELS.channelId = CHANNELS.channelId " + "WHERE CHANNELS.Type = 'public' AND"
				+ " USERCHANNELS.userName = ? ";

		PreparedStatement pstatement;
		ArrayList<Channel> usersChannelsArrayList = new ArrayList<Channel>();

		try {
			pstatement = connection.prepareStatement(sql);
			pstatement.setString(1, /* "sagi" */ requestingUsername);

			ResultSet userChannelsIds = pstatement.executeQuery();

			while (userChannelsIds.next() != false) {

				usersChannelsArrayList.add(buildChannelFromRecord(userChannelsIds));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return usersChannelsArrayList.toArray(new Channel[usersChannelsArrayList.size()]);

	}
	
	/**
	 * Builds channel object from result set recieved from the sql query.
	 * 
	 * @param usersChannels
	 * 			the resultset recieved from the query.
	 * @returns channel object representing a public channel.
	 * 			
	 */
	private Channel buildChannelFromRecord(ResultSet usersChannels) {
		// building the channel object

		Channel channelToBuild = new Channel();

		try {
			channelToBuild.setChannelName(usersChannels.getString("channelName"));
			channelToBuild.setChannelId(usersChannels.getInt("channelId"));

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// returning the topic object

		return channelToBuild;
	}
	
	/**
	 * Basic doPost method.
	 * 
	 * @param request
	 * 			the request from the client side.
	 * 		  response
	 * 			the response to contact the client side.
	 * @throws ServletException, IOException	
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.doPost(request, response);
	}
}
