package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.TransferFailourException;
import com.db.awmd.challenge.repository.AccountsRepository;
import lombok.Getter;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountsService {

  @Getter
  private final AccountsRepository accountsRepository;
  
  @Getter
  private final NotificationService notificationService;
  
  private final String ACCOUNT_DEBIT_MESSAGE = "Your Acount has been debited with amount  :";
  private final String ACCOUNT_CREDIT_MESSAGE = "Your Account has been credited with amount  :";
  @Autowired
  public AccountsService(AccountsRepository accountsRepository,NotificationService notificationService) {
    this.accountsRepository = accountsRepository;
    this.notificationService = notificationService;
  }

  public void createAccount(Account account) {
    this.accountsRepository.createAccount(account);
  }

  public Account getAccount(String accountId) {
    return this.accountsRepository.getAccount(accountId);
  }
  
  public Account transferAmount(Account fromAccount , String secondAcc) throws Exception{
	  
	  Account firstAccDetail = getAccount(fromAccount.getAccountId());
	  BigDecimal amountAfterDeduction =  firstAccDetail.getBalance().subtract(fromAccount.getBalance());
	  
	  if(amountAfterDeduction.compareTo(new BigDecimal(0.0)) >= 0 ){
		  Account secondAccDetail = getAccount(secondAcc);
		  firstAccDetail.setBalance(firstAccDetail.getBalance().subtract(fromAccount.getBalance()));
		  secondAccDetail.setBalance(secondAccDetail.getBalance().add(fromAccount.getBalance()));
		  
		  firstAccDetail = this.accountsRepository.updateAccount(firstAccDetail);
		  notificationService.notifyAboutTransfer(firstAccDetail, ACCOUNT_DEBIT_MESSAGE + fromAccount.getBalance().toString());
		  secondAccDetail = this.accountsRepository.updateAccount(secondAccDetail);
		  notificationService.notifyAboutTransfer(secondAccDetail, ACCOUNT_CREDIT_MESSAGE + fromAccount.getBalance().toString());
		  
	  }else{
		  throw new TransferFailourException("Transfer is not settled due to some problem. Please contact to Administrator");
	  }
	  
	  return firstAccDetail;
  }
}
