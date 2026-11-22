package ma.emsi.ebankservice.service;


import ma.emsi.ebankservice.entities.BankAccount;
import ma.emsi.ebankservice.repository.BankAccountRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EbankService {
    private final BankAccountRepository accountRepository;
    private final CustomerLookupService customerLookupService;

    public EbankService(BankAccountRepository accountRepository,
                        CustomerLookupService customerLookupService){
        this.accountRepository=accountRepository;
        this.customerLookupService = customerLookupService;
    }

    public List<BankAccount> getAllBankAccounts(){
        return accountRepository.findAll().stream()
                .map(this::attachCustomer)
                .collect(Collectors.toList());
    }
    public BankAccount getBankAccountById(String id){
        return attachCustomer(accountRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Account not found")));
    }

    public BankAccount save(BankAccount bankAccount) {
        bankAccount.setId(UUID.randomUUID().toString());
        bankAccount.setCreatedAt(new Date());
        return attachCustomer(accountRepository.save(bankAccount));
    }

    private BankAccount attachCustomer(BankAccount bankAccount) {
        bankAccount.setCustomer(customerLookupService.findCustomerById(bankAccount.getCustomerId()));
        return bankAccount;
    }

}
