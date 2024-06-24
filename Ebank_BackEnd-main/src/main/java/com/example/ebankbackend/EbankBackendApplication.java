package com.example.ebankbackend;

import com.example.ebankbackend.dtos.BankAccountDTO;
import com.example.ebankbackend.dtos.CurrentBankAccountDTO;
import com.example.ebankbackend.dtos.CustomerDTO;
import com.example.ebankbackend.dtos.SavingBankAccountDTO;
import com.example.ebankbackend.entites.*;
import com.example.ebankbackend.enumes.AccountStatus;
import com.example.ebankbackend.enumes.OperationType;
import com.example.ebankbackend.exception.BalanceNotSufficentException;
import com.example.ebankbackend.exception.BankAccountNotFoundException;
import com.example.ebankbackend.exception.CustomerNotFoundException;
import com.example.ebankbackend.repositories.AccountOperationRepository;
import com.example.ebankbackend.repositories.BankAccountRepository;
import com.example.ebankbackend.repositories.CustomerRepository;
import com.example.ebankbackend.services.BankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class EbankBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbankBackendApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(BankAccountService bankAccountService) {
        return args -> {
            Stream.of("hassan","Imane","Mohamed").forEach(name->{
                CustomerDTO customer=new CustomerDTO();
                customer.setName(name);
                customer.setEmail(name+"@gmail.com");
                bankAccountService.saveCustomer(customer);
                    });
            bankAccountService.ListCustomer().forEach(customer -> {
                try {
                    bankAccountService.saveCurrentBankAccount(Math.random()*90000,9000,customer.getId());
                    bankAccountService.saveSavingBankAccount(Math.random()*90000,5.5,customer.getId());
                    List<BankAccountDTO> bankAccounts=bankAccountService.bankAccountList();
for(BankAccountDTO bankAccount:bankAccounts) {
    for (int i = 0; i < 10; i++) {
        String accountId;
        if(bankAccount instanceof SavingBankAccountDTO){
            accountId=((SavingBankAccountDTO) bankAccount).getId();
        }else {
            accountId=((CurrentBankAccountDTO) bankAccount).getId();

        }
        bankAccountService.credit(accountId, 10000 + Math.random() * 120000, "credit");
        bankAccountService.debit(accountId, 1000 + Math.random() * 9000, "Debit");

    }

};
                } catch (CustomerNotFoundException e) {
                    e.printStackTrace();
                } catch (BankAccountNotFoundException | BalanceNotSufficentException e) {
                    throw new RuntimeException(e);
                }

            });
        };
    }

    //@Bean
    CommandLineRunner start(CustomerRepository customerRepository,
                            BankAccountRepository bankAccountrepository,
                            AccountOperationRepository accountOperationRepository
    ) {
        return args -> {
            Stream.of("Hassan", "Ayoub", "Hamza").forEach(name -> {
                Customer customer = new Customer();
                customer.setName(name);
                customer.setEmail(name + "@gmail.com");
                customerRepository.save(customer);
            });
            customerRepository.findAll().forEach(cust -> {
                CurrentAccount currentAccount = new CurrentAccount();
                currentAccount.setId(UUID.randomUUID().toString());
                currentAccount.setBalance(Math.random() * 90000);
                currentAccount.setCreatedAt(new Date());
                currentAccount.setStatus(AccountStatus.CREATED);
                currentAccount.setCustomer(cust);
                currentAccount.setOverDraft(9000);
                bankAccountrepository.save(currentAccount);


                SavingAccount savingAccount = new SavingAccount();
                savingAccount.setId(UUID.randomUUID().toString());
                savingAccount.setBalance(Math.random() * 90000);
                savingAccount.setCreatedAt(new Date());
                savingAccount.setStatus(AccountStatus.CREATED);
                savingAccount.setCustomer(cust);
                savingAccount.setInterestRate(9000);
                bankAccountrepository.save(savingAccount);
            });

            bankAccountrepository.findAll().forEach(acc -> {
                for (int i = 0; i < 10; i++) {
                    AccountOperation accountOperation = new AccountOperation();
                    accountOperation.setOperationDate(new Date());
                    accountOperation.setAmount(Math.random() * 12000);
                    accountOperation.setType(Math.random() > 0.5 ? OperationType.DEBIT : OperationType.CREDIT);
                    accountOperation.setBankAccount(acc);
                    accountOperationRepository.save(accountOperation);
                }


            });
        };
    }
}



