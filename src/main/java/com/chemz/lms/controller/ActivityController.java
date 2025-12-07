package com.chemz.lms.controller;

import com.chemz.lms.dto.ActivityDTO;
import com.chemz.lms.dto.FileRequest;
import com.chemz.lms.dto.StudentActivityDTO;
import com.chemz.lms.model.Activity;
import com.chemz.lms.model.ActivityType;
import com.chemz.lms.service.ActivityService;
import com.chemz.lms.service.CourseService;
import com.chemz.lms.service.S3Service;
import org.springframework.http.HttpStatus;
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

    // GET all activities for a specific course
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<ActivityDTO>> getActivitiesByCourse(@PathVariable Long courseId) {
        courseService.getCourseById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id " + courseId));

        List<ActivityDTO> activities = activityService.getActivitiesByCourse(courseId);
        return ResponseEntity.ok(activities);
    }

    // POST create a new activity for a specific course
    @PostMapping("/course/{courseId}")
    public ResponseEntity<?> createActivity(
            @PathVariable Long courseId,
            @RequestBody Activity activityRequest) {

        // Check if activityNumber is already used for this type in this course
        Integer activityNumber = activityRequest.getActivityNumber();
        ActivityType type = activityRequest.getType();

        if (activityNumber != null && activityService.existsByCourseAndTypeAndActivityNumber(courseId, type, activityNumber)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "Activity number " + activityNumber + " already exists for type " + type + " in this course."
                    ));
        }

        ActivityDTO dto = activityService.createActivity(courseId, activityRequest);
        return ResponseEntity.ok(dto);
    }

    // Presigned URL endpoints
    @PostMapping("presigned-url/document")
    public Map<String, String> createDocumentPresignedUrl(@RequestBody FileRequest req) {
        String folderName = S3Service.FOLDER_DOCUMENTS;

        String url = s3Service.generatePresignedUrl(
                folderName,
                req.getFileName(),
                req.getContentType()
        );

        String finalS3Key = folderName + "/" + req.getFileName();

        return Map.of(
                "url", url,
                "fileName", req.getFileName(),
                "s3Key", finalS3Key
        );
    }

    @PostMapping("presigned-url/proof")
    public Map<String, String> createProofPresignedUrl(@RequestBody FileRequest req) {
        String folderName = S3Service.FOLDER_PROOFS;

        String url = s3Service.generatePresignedUrl(
                folderName,
                req.getFileName(),
                req.getContentType()
        );

        String finalS3Key = folderName + "/" + req.getFileName();

        return Map.of(
                "url", url,
                "fileName", req.getFileName(),
                "s3Key", finalS3Key
        );
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

// --- StudentActivity endpoints ---

    @GetMapping("/{activityId}/retries/{studentId}")
    public ResponseEntity<Integer> getRetries(
            @PathVariable Long activityId,
            @PathVariable Long studentId
    ) {
        int retries = activityService.getRetries(studentId, activityId);
        return ResponseEntity.ok(retries);
    }

    @GetMapping("/{activityId}/score/{studentId}")
    public ResponseEntity<Integer> getScore(
            @PathVariable Long activityId,
            @PathVariable Long studentId
    ) {
        int score = activityService.getScore(studentId, activityId);
        return ResponseEntity.ok(score);
    }

    @PatchMapping("/{activityId}/retries/{studentId}")
    public ResponseEntity<StudentActivityDTO> incrementRetries(
            @PathVariable Long activityId,
            @PathVariable Long studentId
    ) {
        StudentActivityDTO dto = activityService.incrementRetries(studentId, activityId);
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/{activityId}/score/{studentId}")
    public ResponseEntity<StudentActivityDTO> setScore(
            @PathVariable Long activityId,
            @PathVariable Long studentId,
            @RequestParam int score
    ) {
        StudentActivityDTO dto = activityService.setScore(studentId, activityId, score);
        return ResponseEntity.ok(dto);
    }

}
