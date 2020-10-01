package com.example.myproject.result;

public class DeleteResult{
  private boolean delete_success;
  private String err_msg;



  public String getErr_msg(){
    return this.err_msg;
  }

  public void setErr_msg(String str){
    this.err_msg = str;
  }

  public void addMessage(String msg){
    this.err_msg += msg;
  }

  public boolean isDelete_success() {
	return delete_success;
  }

  public void setDelete_success(boolean delete_success) {
	this.delete_success = delete_success;
  }

}
