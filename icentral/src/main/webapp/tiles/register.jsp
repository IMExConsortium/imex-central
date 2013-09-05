<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
 <div class="main-width">
 <h1>Sign up for ImexCentral Account</h1>  

 <p>Already have an account? <a href="javascript:YAHOO.mbi.login.sendUrlFragment();">Log in</a></p>
 <!-- <p>Forgotten your password? <a href="mailto:dip@mbi.ucla.edu">Contact Us</a></p> -->
 <p>In order to gain full access to ImexCentral you must register.</p>
 <s:form action="register">
  <s:hidden theme="simple" name="op" value="reg" />
  
  <div class="pub-edit-head">
   <h2>User Registraton</h2>
  </div>
  <div class="top-padding">
   <fieldset>
    <legend><h3>Login</h3></legend>
    <div class="field-padding">
    <strong>User Name</strong>
    <s:if test="hasFieldErrors()">
     <s:if test="fieldErrors['user.login']!=null">  
      <div id="errorDiv" style="padding-left: 10px; margin-bottom: 5px">
       <span class="error">
        <span class="errorMessage">
         <s:property value="fieldErrors['user.login'][0]" />
        </span>
       </span>
       </div>
     </s:if>
    </s:if>
    <s:textfield theme="simple" 
                 name="user.login" size="32" maxlength="32" />
    <s:if test="hasFieldErrors()">
     <s:if test="fieldErrors['pass1']!=null">  
      <div id="errorDiv" style="padding-left: 10px; margin-bottom: 5px">
        <span class="error">
        <span class="errorMessage">
         <s:property value="fieldErrors['pass1'][0]" />
        </span>
       </span>
      </div>
     </s:if>
    </s:if>
   </div>
     
   <div class="field-padding"><strong>Password</strong>
     <s:password theme="simple"
          name="pass0" size="24" maxlength="24" />
   </div>
   <div class="field-padding"><strong>Password</strong> (retype) 
     <s:password theme="simple"
         name="pass1" size="24" maxlength="24" />
   </div>
   </fieldset>
  </div>
  
  <div class="top-padding">
   <fieldset>
   <legend><h3>Contact</h3></legend>
     
   <div class="field-padding"><strong>First Name</strong>
    <s:textfield labelSeparator="" theme="simple"
      name="user.firstName" size="32" maxlength="64" />
   </div>
    <div class="field-padding"><strong>Last Name<strong>            
     <s:textfield labelSeparator="" theme="simple"
       name="user.lastName" size="32" maxlength="64" />
    </div>
    <div class="field-padding"><strong>Organization</strong>
     <s:textfield labelSeparator="" theme="simple"
       name="user.affiliation" size="32" maxlength="64" />
    </div>
    <div class="field-padding"><strong>E-Mail</strong>
     <s:textfield labelSeparator="" theme="simple"
       name="user.email" size="32" maxlength="64" />
    </div>
   </fieldset>
  </div>
  
   <s:if test="hasFieldErrors()">
    <s:if test="fieldErrors['agree']!=null"> 
      <div id="errorDiv" style="padding-left: 10px; margin-bottom: 5px">
       <span class="error">
        <span class="errorMessage">
         <s:property value="fieldErrors['agree'][0]" />
        </span>
       </span>
      </div>
    </s:if>
   </s:if>
  
                        
  <div class="top-padding">
   <fieldset>
   <legend><h3>Terms of Use</h3></legend>
   <p><s:checkbox theme="simple" name="agree" value="false"/> 
  
     I represent that I have read and accepted the  
     <a href="page?id=termsofuse&mdf=0:2:0&mst=3:0:0">Terms of Use</a>.</p>
  </div>


  <h3>Type Both Words Below</h3>
  <div class="center captcha-width">
   <s:if test="hasActionErrors()">
    <div id="errorDiv" style="padding-left: 10px; margin-bottom: 5px">
     <span class="error">
      <s:iterator value="actionErrors">
      <span class="errorMessage"><s:property escape="false" /></span>
      </s:iterator>
     </span>
    </div>
   </s:if> 
   <script type="text/javascript"                             
      src="${icentral.recaptcha.recaptchaServer}/challenge?k=${icentral.recaptcha.publicKey}">
   </script>
   <noscript>
    <iframe src="${icentral.recaptcha.recaptchaServer}/noscript?k=${icentral.recaptcha.publicKey}" 
       height="300" width="500" frameborder="0">
    </iframe>
    <textarea name="recaptcha_challenge_field" rows="3" cols="40"></textarea>
    <input type='hidden' name='recaptcha_response_field' value='manual_challenge' />
   </noscript>
  </div>
 
  <div  class="clogin">
    <p><s:submit theme="simple" name="Submit" value="Submit" /></p>
    <p>You will be notified about account
    creation and the initial password by an <em>e-mail</em>
    sent to the address specified above.</p>
    </div>
 </s:form>      

</div>
