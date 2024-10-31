import { cookies } from 'next/headers'
import { type NextRequest, NextResponse } from 'next/server'

const protectedRoutes = ['/dashboard']
const publicRoutes = ['/']

const middleware = async (req: NextRequest): Promise<NextResponse> => {
  const path = req.nextUrl.pathname
  const isProtectedRoute = protectedRoutes.some((protectedRoute) =>
    path.startsWith(protectedRoute)
  )
  const isPublicRoute = publicRoutes.includes(path)

  const cookie = (await cookies()).get('session')?.value

  if (isProtectedRoute && !cookie) {
    return NextResponse.redirect(new URL('/', req.nextUrl))
  }

  if (
    isPublicRoute &&
    cookie &&
    !req.nextUrl.pathname.startsWith('/dashboard')
  ) {
    return NextResponse.redirect(new URL('/dashboard', req.nextUrl))
  }

  return NextResponse.next()
}

// Routes Middleware should not run on
export const config = {
  matcher: ['/((?!api|_next/static|_next/image|.*\\.png$).*)']
}

export default middleware
