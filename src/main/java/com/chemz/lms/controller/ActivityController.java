    package com.chemz.lms.controller;

    import com.chemz.lms.dto.ActivityDTO;
    import com.chemz.lms.dto.FileRequest;
    import com.chemz.lms.model.Activity;
    import com.chemz.lms.model.Course;
    import com.chemz.lms.service.S3Service;
    import com.chemz.lms.service.ActivityService;
    import com.chemz.lms.service.CourseService;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;
    import java.util.Map;

    @RestController
    @RequestMapping("/api/activities")
    public class ActivityController {

        private final ActivityService activityService;
        private final CourseService courseService;
        private final S3Service s3Service;

        public ActivityController(ActivityService activityService,
                                  CourseService courseService,
                                  S3Service s3Service) {
            this.activityService = activityService;
            this.courseService = courseService;
            this.s3Service = s3Service;
        }

        // GET all activities for a specific course (DTO)
        @GetMapping("/course/{courseId}")
        public ResponseEntity<List<ActivityDTO>> getActivitiesByCourse(@PathVariable Long courseId) {
            courseService.getCourseById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found with id " + courseId));

            List<ActivityDTO> activities = activityService.getActivitiesByCourse(courseId);
            return ResponseEntity.ok(activities);
        }

        // POST create a new activity for a specific course
        @PostMapping("/course/{courseId}")
        public ResponseEntity<ActivityDTO> createActivity(
                @PathVariable Long courseId,
                @RequestBody Activity activityRequest) {

            ActivityDTO dto = activityService.createActivity(courseId, activityRequest);
            return ResponseEntity.ok(dto);
        }

        // Presigned URL generation
        @PostMapping("presigned-url")
        public Map<String, String> createPresignedUrl(@RequestBody FileRequest req) {
            String url = s3Service.generatePresignedUrl(req.getFileName(), req.getContentType());
            return Map.of("url", url, "fileName", req.getFileName());
        }

        // PUT update an existing activity
        @PutMapping("/{id}")
        public ResponseEntity<ActivityDTO> updateActivity(
                @PathVariable Long id,
                @RequestBody Activity activityRequest) {

            ActivityDTO updated = activityService.updateActivity(id, activityRequest);
            return ResponseEntity.ok(updated);
        }

        // DELETE an activity by id
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
            activityService.deleteActivity(id);
            return ResponseEntity.noContent().build();
        }
    }
