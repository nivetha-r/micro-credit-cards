package com.creditcard.creditcards.service;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.creditcard.creditcards.constant.Constant;
import com.creditcard.creditcards.dto.TransactionListResponseDto;
import com.creditcard.creditcards.dto.TransactionRequestDto;
import com.creditcard.creditcards.dto.TransactionResponseDto;
import com.creditcard.creditcards.entity.Transaction;
import com.creditcard.creditcards.entity.User;
import com.creditcard.creditcards.exception.TransactionNotFoundException;
import com.creditcard.creditcards.repository.CreditCardRepository;
import com.creditcard.creditcards.repository.TransactionRepository;
import com.creditcard.creditcards.repository.UserRepository;

/**
 * This API is used to get the monthly transactions of the user by giving the
 * userId
 * 
 * @author Nivetha
 *
 */
@Service
public class TransactionServiceImpl implements TransactionService {

	private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

	/**
	 * This will inject all the implementations in the transactionRepository
	 */

	@Autowired
	TransactionRepository transactionRepository;

	/**
	 * This will inject all the implementations in the userRepository
	 */

	@Autowired
	UserRepository userRepository;

	/**
	 * This will inject all the implementations in the creditCardRepository
	 */

	@Autowired
	CreditCardRepository creditCardRepository;

	/**
	 * This API is used to get the monthly transactions for the user by giving the
	 * userId
	 * 
	 * @param transactionRequestDto This dto contains the userId,month and the year
	 * 
	 * @return This returns the transactionResponseDto which includes the
	 *         transactionId, amount,status,description along with the statusCode
	 *         and message
	 * 
	 * @throws TransactionNotFoundException occurs when transactions not found for
	 *                                      the user
	 */

	@Override
	public TransactionResponseDto monthlyTransactions(TransactionRequestDto transactionRequestDto)
			throws TransactionNotFoundException {
		logger.info("to get monthly transactions");
		String month = String.format("%02d", Integer.parseInt(transactionRequestDto.getMonth()));
		Integer year = transactionRequestDto.getYear();
		LocalDate endDate = Year.parse(transactionRequestDto.getYear().toString())
				.atMonth(Integer.parseInt(transactionRequestDto.getMonth())).atEndOfMonth();
		LocalDate startDate = LocalDate.parse(year + "-" + month + "-" + "01");

		Optional<User> user = userRepository.findById(transactionRequestDto.getUserId());
		if (user.isPresent()) {
		Long cardId = user.get().getCreditCard().getCardId();
		logger.info("getting card details");
		List<Transaction> transactions = transactionRepository.findAllByCreditCardCardIdAndDateBetween(cardId,
				startDate, endDate);
		List<TransactionListResponseDto> transactionListResponseDtoList = new ArrayList<>();
		transactions.forEach(transaction -> {
			TransactionListResponseDto transactionListResponseDto = new TransactionListResponseDto();
			transactionListResponseDto.setAmount(transaction.getAmount());
			transactionListResponseDto.setDate(transaction.getDate());
			transactionListResponseDto.setDescription(transaction.getDescription());
			transactionListResponseDto.setStatus(transaction.getStatus());
			transactionListResponseDto.setTransactionId(transaction.getTransactionId());
			transactionListResponseDtoList.add(transactionListResponseDto);
		});
		
		if (transactionListResponseDtoList.isEmpty()) {
			throw new TransactionNotFoundException(Constant.TRANSACTION_NOT_FOUND);
		} else {
			TransactionResponseDto transactionResponseDto = new TransactionResponseDto();
			transactionResponseDto.setTransactionListResponseDto(transactionListResponseDtoList);
			transactionResponseDto.setStatuscode(Constant.ACCEPTED);
			transactionResponseDto.setMessage(Constant.SUCCESS);

			return transactionResponseDto;

		}
		} else {
			return null;
		}

	}
}
