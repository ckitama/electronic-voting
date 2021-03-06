package com.web_application_development.evoting.services;

import com.web_application_development.evoting.entities.UserStatistics;
import com.web_application_development.evoting.repositories.UserStatisticsRepository;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class UserStatisticsService {

    private static final Logger logger = LogManager.getLogger(CandidateService.class);

    @Autowired
    private final UserStatisticsRepository userStatisticsRepository;

    public UserStatisticsService(UserStatisticsRepository userStatisticsRepository) {
        this.userStatisticsRepository = userStatisticsRepository;
    }


    private boolean sessionExists(String session_id) {
        return userStatisticsRepository.sessionExists(session_id);
    }

    private boolean ipLoggedToday(String ip, String browser) {
        logger.debug("Request: userStatisticsRepository.ipLoggedToday(ip, browser, LocalDate.now()): " + ip + " " + browser);
        return userStatisticsRepository.ipLoggedToday(ip, browser, LocalDate.now());
    }

    public void saveUserStatistics(HttpServletRequest request, String landing_page) {
        String session_id = RequestContextHolder.currentRequestAttributes().getSessionId();
        String ip = request.getRemoteAddr();
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        String browser = userAgent.getBrowser().getName();
        if (!sessionExists(session_id) && !ipLoggedToday(ip, browser)) {
            logger.debug("Request: userStatisticsRepository.save(setUserStatistics(landing_page, session_id, ip, browser)): " + request + " " + landing_page);
            userStatisticsRepository.save(setUserStatistics(landing_page, session_id, ip, browser));
        }
    }

    private UserStatistics setUserStatistics(String landing_page, String session_id, String ip, String browser) {
        UserStatistics userStatistics = new UserStatistics();
        userStatistics.setBrowser(browser);
        userStatistics.setSessionId(session_id);
        userStatistics.setLandingPage(landing_page);
        userStatistics.setIp(ip);
        userStatistics.setTimestamp(LocalDateTime.now());
        return userStatistics;
    }

    public List<String> getTopBrowsers() {
        return userStatisticsRepository.getTopBrowsers();
    }

    public List<String> getTopLandingPages() {
        return userStatisticsRepository.getTopLandingPages();
    }

    public long getUniqueVisitorsToday() {
        return userStatisticsRepository.getUniqueVisitorsToday();
    }
}
