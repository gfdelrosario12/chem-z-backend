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
    import java.util.Optional;

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
                    "s3Key", finalS3Key // Client stores this key in the Activity model
            );
        }

        /**
         * Endpoint for uploading proof/screenshot files.
         * Uses the hardcoded "proofs" S3 folder.
         */
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
                    "s3Key", finalS3Key // Client stores this key in the Activity model
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

            // NO NEED to fetch the Activity here.
            // NO NEED to manually call S3Service.deleteFile().

            // DELEGATE: The ActivityService now handles the entire deletion process:
            // 1. Fetching the Activity by ID (for its fileUrl).
            // 2. Extracting the S3 Key from the fileUrl.
            // 3. Calling S3Service.deleteFile(key).
            // 4. Deleting the Activity record from the database.

            activityService.deleteActivity(id);

            // This is all the controller needs to do for a DELETE operation.
            return ResponseEntity.noContent().build();
        }
    }
