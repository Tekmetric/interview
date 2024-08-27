import React from "react";
import { render, screen, fireEvent, act } from "@testing-library/react";
import EventForm from "./EventForm";
import { Formik } from "formik";

describe("EventForm Component", () => {
  const initialValues = {
    title: "",
    eventDatetime: "",
    description: "",
  };

  const handleSubmit = jest.fn();

  const renderComponent = () =>
    render(
      <Formik initialValues={initialValues} onSubmit={handleSubmit}>
        {(formik) => <EventForm formik={formik} />}
      </Formik>
    );

  test("renders form fields correctly", () => {
    renderComponent();

    expect(screen.getByLabelText(/Event Title/i)).toBeDefined();
    expect(screen.getByLabelText(/Event Date/i)).toBeDefined();
  });

  test("handles input changes", () => {
    renderComponent();

    const titleInput = screen.getByLabelText(/Event Title/i);
    const dateInput = screen.getByLabelText(/Event Date/i);

    act(() => {
      fireEvent.change(titleInput, { target: { value: "New Event" } });
      fireEvent.change(dateInput, { target: { value: "2023-10-10T10:00" } });
    });

    expect(titleInput.nodeValue).toContain("New Event");
    expect(dateInput.nodeValue).toContain("2023-10-10T10:00");
  });
});
