package com.interview.resource;

import com.google.common.collect.Lists;
import com.interview.entities.Job;
import com.interview.repositories.JobRepository;
import com.interview.requests.JobCreateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class JobController {
    @Autowired
    JobRepository jobRepository;

    @GetMapping("/job/{id}")
    public Job getJob(@PathVariable Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("No such job exists for id %s", id)));
    }

    @GetMapping("/jobs")
    public List<Job> getJobs() {
        return Lists.newArrayList(jobRepository.findAll());
    }

    @PostMapping("/job")
    public Job createJob(@RequestBody JobCreateRequest jobCreateRequest) {
        Job job = new Job();
        job.setCarId(jobCreateRequest.getCarId());
        job.setServiceId(jobCreateRequest.getServiceId());

        return jobRepository.save(job);
    }

    @PutMapping("/updateJob")
    public Job updateJob(@RequestBody Job job) {
        Optional<Job> dbJobOptional = jobRepository.findById(job.getId());

        if (dbJobOptional.isPresent()) {
            return jobRepository.save(job);
        } else {
           throw new NoSuchElementException(String.format("Could not update job with id %s because that job does not exist", job.getId()));
        }
    }

    @DeleteMapping("/deleteJob/{id}")
    public String deleteJob(@PathVariable Long id) {
        jobRepository.deleteById(id);
        return String.format("Job with id %s was successfully deleted", id);
    }
}