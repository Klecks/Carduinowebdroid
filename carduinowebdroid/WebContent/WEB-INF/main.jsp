<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage="" %>
<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<title>CarDuinoDroid</title>
<link rel="icon" href="static/favicon.ico" type="image/x-icon" />
<!--JQuery Import-->
<link rel="stylesheet" href="static/jquery-ui-1.10.3.custom/css/ui-lightness/jquery-ui-1.10.3.custom.css" />
<script src="static/jquery-1.9.1.js"></script>
<script src="static/jquery-ui.js"></script>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/lib/tags/customTag.tld" prefix="ct" %>

<!--Custom Stylesheet-->

<link rel="stylesheet" href="static/style.css" />

<!--Custom jS-->

<script src="static/main.js"></script>
<script src="static/chat.js"></script>

</head>
<body>

<table id="main_table">
	<tr>
    	<td id="main_table_sidebar_left">
        	<div id="main_chat_container">
        		<textarea type="text" id="main_chat" readonly></textarea>
          	  	<textarea type="text" name="chat" id="main_chat_textinput" maxlength="256"></textarea>
           		<div id="main_chat_chars">256</div>
            	<button id="main_chat_speak">Chat</button>
          	</div>
        </td>
        <td><button id="main_close_left" class="ui-icon ui-icon-triangle-1-w"></button> <button id="main_open_left" class="ui-icon ui-icon-triangle-1-e"></button></td>
        <td id="main_table_stream">
        
        	<div id="main_stream">Stream</div>
        	<div id="main_controls">
            	<table id="main_control_table">
            		<tr>
            			<td id="main_slider">
                			<center><div class="slider-vertical" id="main_maxspeed"></div></center>
                			Max. Speed
                		</td>
                		<td id="main_slider">
                			<center><div class="slider-vertical" id="main_steerangle"></div></center>
                			Steering Angle
                		</td>
                        <td id="main_steering">
                            <table id="main_steering_table">
                                <tr>
                                    <td>&nbsp;
                                        
                                    </td>                                    
                                    <td>
                                        <button value="Up" type="button" id="main_steering_button_up">Up</button>
                                    </td>
                                    <td>&nbsp;
                                        
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        <button value="Left" type="button" id="main_steering_button_left">Left</button>
                                    </td>
                                    <td>
                                        <button value="Down" type="button" id="main_steering_button_down">Down</button>
                                    </td>
                                    <td>
                                        <button value="Right" type="button" id="main_steering_button_right">Right</button>
                                    </td>
                                </tr>
                            </table>
                        </td>
                        <td id="main_gadget">
                            <button value="Horn" type="button" id="main_gadget_button_horn">Horn</button><br>
                            <button value="Light" type="button" id="main_gadget_button_light">Light</button>
                        </td>
                	</tr>
            	</table>
            </div>
            
        </td>
        <td><button id="main_close_right" class="ui-icon ui-icon-triangle-1-e"></button> <button id="main_open_right" class="ui-icon ui-icon-triangle-1-w"></button></td>
        <td id="main_table_sidebar_right">
        	<div id="main_q_container">	
               	<div id="main_q">Q </div>
                <form method="POST">
                    <input type="hidden" name="action" value="enqueue"/>
                    <input id="main_qsubmit" type="submit" value="Enqueue" />
                </form>
                <form method="POST">
                	<input type="hidden" name="action" value="logout"/>
                	<input id="main_logout" type="submit" value="Logout" />
                </form>
                <c:set var="isAdmin"><ct:isAdmin /></c:set>
                <c:if test="${isAdmin == 1}">
                	<a href="admin.jsp?menu=1">
                		<button id="main_admin">Admin</button>
                	</a>
                </c:if>
            </div> 
        </td>
    </tr>
</table>

<br>

</body>
</html>