// package com.safeg.user.controller;

// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.safeg.user.vo.CampaignVO;

// @RestController
// @RequestMapping("/api/events")
// public class EventController {
//      @Autowired
//     private EventService eventService; // DB에서 이벤트 가져오는 서비스

//     @GetMapping
//     public List<Map<String, Object>> getEvents() {
//         // 서비스에서 DB 이벤트를 가져와서 FullCalendar 형식에 맞게 가공
//         CampaignVO campaignSelect = mainService.campaignSelect(id); // Event는 너가 만든 DB 모델이라고 가정
//         List<Map<String, Object>> calendarEvents = new ArrayList<>();

//         for (Event event : eventsFromDb) {
//             Map<String, Object> eventMap = new HashMap<>();
//             eventMap.put("id", event.getId());
//             eventMap.put("title", event.getTitle());
//             eventMap.put("start", event.getStartDate().toString()); // ISO 8601 형식 (YYYY-MM-DD)
//             if (event.getEndDate() != null) {
//                 eventMap.put("end", event.getEndDate().toString());
//             }
//             // 필요한 다른 속성도 추가할 수 있어 (예: color, url 등)
//             calendarEvents.add(eventMap);
//         }
//         return calendarEvents;
//     }
// }