package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.TransferFailourException;
import com.db.awmd.challenge.service.AccountsService;
import java.math.BigDecimal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsServiceTest {

  @Autowired
  private AccountsService accountsService;

  @Test
  public void addAccount() throws Exception {
    Account account = new Account("Id-123");
    account.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account);

    assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
  }

  @Test
  public void addAccount_failsOnDuplicateId() throws Exception {
    String uniqueId = "Id-" + System.currentTimeMillis();
    Account account = new Account(uniqueId);
    this.accountsService.createAccount(account);

    try {
      this.accountsService.createAccount(account);
      fail("Should have failed when adding duplicate account");
    } catch (DuplicateAccountIdException ex) {
      assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
    }

  }
  
  @Test
  public void transferAmount() throws Exception{
	  Account fromAccount = new Account("Id-21",new BigDecimal(10000));
	  Account toAccount = new Account("Id-22",new BigDecimal(1000));
	  this.accountsService.createAccount(fromAccount);
	  this.accountsService.createAccount(toAccount);
	  String toAccountID = "Id-22";
	  try{
		  Account transferDtls = new Account("Id-21",new BigDecimal(2000));
		  Account acc = accountsService.transferAmount(transferDtls, toAccountID);
		  assertThat(this.accountsService.getAccount("Id-21").getBalance().equals(new BigDecimal(8000)));
	  }catch(TransferFailourException te){
		  assertThat(te.getMessage()).isEqualTo("Transfer is not settled due to some problem. Please contact to Administrator");
	  }
	  
  }
}
