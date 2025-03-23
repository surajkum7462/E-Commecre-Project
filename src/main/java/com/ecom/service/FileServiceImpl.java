/*
 * package com.ecom.service;
 * 
 * import java.io.InputStream;
 * 
 * import org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.beans.factory.annotation.Value; import
 * org.springframework.stereotype.Service; import
 * org.springframework.util.ObjectUtils; import
 * org.springframework.web.multipart.MultipartFile;
 * 
 * import com.amazonaws.services.s3.AmazonS3; import
 * com.amazonaws.services.s3.model.ObjectMetadata; import
 * com.amazonaws.services.s3.model.PutObjectRequest; import
 * com.amazonaws.services.s3.model.PutObjectResult;
 * 
 * @Service public class FileServiceImpl implements FileService {
 * 
 * @Autowired public AmazonS3 amazonS3;
 * 
 * @Value("${aws.s3.bucket.category}") private String categoryBucket;
 * 
 * @Value("${aws.s3.bucket.product}") private String productBucket;
 * 
 * @Value("${aws.s3.bucket.profile}") private String profileBucket;
 * 
 * @Override public Boolean uploadFileS3(MultipartFile file, Integer bucketType)
 * { try {
 * 
 * String bucketName = null; if (bucketType == 1) { bucketName = categoryBucket;
 * } else if (bucketType == 2) { bucketName = productBucket; } else { bucketName
 * = profileBucket; }
 * 
 * String fileName = file.getOriginalFilename(); InputStream inputStream =
 * file.getInputStream(); ObjectMetadata objectMetadata = new ObjectMetadata();
 * objectMetadata.setContentLength(file.getSize());
 * 
 * PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,
 * fileName, inputStream, objectMetadata);
 * 
 * PutObjectResult saveData = amazonS3.putObject(putObjectRequest); if
 * (!ObjectUtils.isEmpty(saveData)) { return true; }
 * 
 * } catch (Exception e) { // TODO: handle exception e.printStackTrace(); }
 * 
 * return false; }
 * 
 * }
 */
package com.ecom.service;

import java.io.InputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.core.sync.RequestBody;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private S3Client s3Client;

    @Value("${aws.s3.bucket.category}")
    private String categoryBucket;

    @Value("${aws.s3.bucket.product}")
    private String productBucket;

    @Value("${aws.s3.bucket.profile}")
    private String profileBucket;

    @Override
    public Boolean uploadFileS3(MultipartFile file, Integer bucketType) {
        try {
            String bucketName = switch (bucketType) {
                case 1 -> categoryBucket;
                case 2 -> productBucket;
                default -> profileBucket;
            };

            String fileName = file.getOriginalFilename();
            InputStream inputStream = file.getInputStream();

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentLength(file.getSize())
                    .build();

            PutObjectResponse response = s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.getSize()));

            return !ObjectUtils.isEmpty(response);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
