package com.freightfox.file.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.freightfox.file.config.SConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
@Service

public class FileService {

	    @Autowired
	    private AmazonS3 amazonS3;

	    @Autowired
	    private SConfig s3Config;

	     //Searches for files in the specified user's folder that match the search term.
	    public List<String> searchFilesList(String userName, String searchTerm) {
	        String userFolder = userName + "/";
	        return amazonS3.listObjects(s3Config.getBucketName(), userFolder)
	                .getObjectSummaries()
	                .stream()
	                .filter(s3ObjectSummary -> s3ObjectSummary.getKey().contains(searchTerm))
	                .map(S3ObjectSummary::getKey)
	                .collect(Collectors.toList());
	    }

}
