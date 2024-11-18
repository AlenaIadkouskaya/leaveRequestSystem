package pl.iodkovskaya.leaveRequestSystem.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.iodkovskaya.leaveRequestSystem.exception.InvalidOperationException;
import pl.iodkovskaya.leaveRequestSystem.model.dto.VacationBalanceDto;
import pl.iodkovskaya.leaveRequestSystem.model.entity.request.RequestEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.vacationbalance.VacationBalanceEntity;
import pl.iodkovskaya.leaveRequestSystem.reposityry.VacationBalanceRepository;

import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
//@AllArgsConstructor
public class VacationBalanceServiceImpl implements VacationBalanceService, RequestListener {
    private final UserService userService;
    private final VacationBalanceRepository vacationBalanceRepository;
    private static final int MONTHLY_VACATION_DAYS_INCREMENT = 2;
    private static final Logger logger = LoggerFactory.getLogger(VacationBalanceServiceImpl.class);
    @Value("${vacation.increment.cron}")
    private String vacationIncrementCron;

    public VacationBalanceServiceImpl(UserService userService, VacationBalanceRepository vacationBalanceRepository,
                                      @Value("${vacation.increment.cron}") String vacationIncrementCron) {
        this.userService = userService;
        this.vacationBalanceRepository = vacationBalanceRepository;
        this.vacationIncrementCron = vacationIncrementCron;
    }

    @Override
    public void addRecord(VacationBalanceDto vacationBalanceDto) {
        UserEntity userById = userService.findUserById(vacationBalanceDto.getUserId());
        if (userById == null) {
            throw new EntityNotFoundException("User not found with ID: " + vacationBalanceDto.getUserId());
        }
        VacationBalanceEntity vacationBalanceEntity = new VacationBalanceEntity(userById,
                vacationBalanceDto.getTotalDays(),
                vacationBalanceDto.getUsedDays(),
                vacationBalanceDto.getHireDate());

        vacationBalanceRepository.save(vacationBalanceEntity);
    }

    @Override
    public void checkRemainderForUser(UserEntity user, Integer countDays) {
        VacationBalanceEntity vacationBalance = vacationBalanceRepository.findByUser(user)
                .orElseThrow(() -> new InvalidOperationException("No data on remaining vacation days"));
        if (vacationBalance.getRemainingDays() < countDays) {
            throw new InvalidOperationException("Insufficient vacation days available. Your remainder: " + vacationBalance.getRemainingDays());
        }
    }

    @Override
    public void decreaseRemainder(UserEntity user, RequestEntity request) {
        int days = getDaysVacation(request);
        VacationBalanceEntity vacationBalance = vacationBalanceRepository.findByUser(user)
                .orElseThrow(() -> new InvalidOperationException("No data on remaining vacation days"));
        vacationBalance.increaseUsedDays(days);
    }

    @Override
    public void increaseRemainder(UserEntity user, RequestEntity request) {
        int days = getDaysVacation(request);
        VacationBalanceEntity vacationBalance = vacationBalanceRepository.findByUser(user)
                .orElseThrow(() -> new InvalidOperationException("No data on remaining vacation days"));
        vacationBalance.decreaseUsedDays(days);
    }

    @Scheduled(cron = "#{@vacationIncrementCron}")
    @Transactional
    @Override
    public void incrementVacationDaysMonthly() {
        //List<VacationBalanceEntity> balances = vacationBalanceRepository.findAll();
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int day = today.getDayOfMonth();
        boolean isLeapYear = Year.isLeap(today.getYear());

        List<VacationBalanceEntity> balances = vacationBalanceRepository.findAllByHireDateMonthAndDay(day, isLeapYear);


        for (VacationBalanceEntity balance : balances) {
            try {
                balance.incrementTotalDays(MONTHLY_VACATION_DAYS_INCREMENT);
                vacationBalanceRepository.save(balance);
                logger.info("Successfully updated vacation balance for user with ID: {}", balance.getUser().getUserId());
            } catch (Exception e) {
                logger.error("Failed to update vacation balance for user with ID: {}. Error: {}", balance.getUser().getUserId(), e.getMessage(), e);
            }
        }
    }

    private static int getDaysVacation(RequestEntity request) {
        int days = (int) (ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1);
        return days;
    }
}
