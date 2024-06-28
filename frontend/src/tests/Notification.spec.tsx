import React from 'react';
import { toast } from 'react-toastify';
import '@testing-library/jest-dom';
import { notifySuccess, notifyError } from '../components/Notification';

jest.mock('react-toastify');

describe('Notification', () => {
  it('displays a success notification', () => {
    const message = 'Success!';
    notifySuccess(message);
    expect(toast.success).toHaveBeenCalledWith(message, {
      position: "top-right",
    });
  });

  it('displays an error notification', () => {
    const message = 'Error!';
    notifyError(message);
    expect(toast.error).toHaveBeenCalledWith(message, {
      position: "top-right",
    });
  });
});
