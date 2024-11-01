'use client'

import { type Id, type TypeOptions, toast } from 'react-toastify'

export const useToastContainer = (): {
  notify: (props: { message: string; type?: TypeOptions }) => Id
} => {
  const notify = ({
    message,
    type = 'success'
  }: {
    message: string
    type?: TypeOptions
  }): Id => toast(message, { type })

  return { notify }
}
