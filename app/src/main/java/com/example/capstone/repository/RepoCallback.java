package com.example.capstone.repository;

public interface RepoCallback<T> {
    void onComplete(Result<T> result);
}
