package com.example.myproject.result;

public class RegistrationResult{
  private boolean registration_success;
  private String err_msg;

  public boolean getRegistration_success(){
    return this.registration_success;
  }

  public void setRegistration_success(boolean tf){
    this.registration_success = tf;
  }

  public String getErr_msg(){
    return this.err_msg;
  }

  public void setErr_msg(String str){
    this.err_msg = str;
  }

  public void addMessage(String msg){
    this.err_msg += msg;
  }
}
