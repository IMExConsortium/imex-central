package edu.ucla.mbi.util.struts2.interceptor;

/* =============================================================================
 # $HeadURL::                                                                  $
 # $Id::                                                                       $
 # Version: $Rev::                                                             $
 #==============================================================================
 #                                                                             $
 # WikiParseAware:  wiki parser target                                         $
 #                                                                             $
 #=========================================================================== */

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

import java.io.InputStream;
import javax.servlet.ServletContext;

import edu.ucla.mbi.util.*;

public interface WikiParseAware{
}