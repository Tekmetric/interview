import { cookies } from 'next/headers'
import { type NextRequest, NextResponse } from 'next/server'

const PROTECTED_ROUTES = ['/dashboard']
const PUBLIC_ROUTES = ['/']
const SESSION_KEY = 'session'

const middleware = async (req: NextRequest): Promise<NextResponse> => {
  const path = req.nextUrl.pathname
  const isProtectedRoute = PROTECTED_ROUTES.some((protectedRoute) =>
    path.startsWith(protectedRoute)
  )
  const isPublicRoute = PUBLIC_ROUTES.includes(path)

  const cookie = (await cookies()).get(SESSION_KEY)?.value

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
