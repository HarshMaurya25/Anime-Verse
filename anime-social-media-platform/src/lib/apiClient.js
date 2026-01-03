import axios from 'axios'

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:11111/api',
  timeout: 12000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Helper to read and write cookies locally
const getCookie = (name) => {
  if (typeof document === 'undefined') return ''
  const match = document.cookie.match(new RegExp(`(?:^|; )${name}=([^;]*)`))
  return match ? decodeURIComponent(match[1]) : ''
}

const setCookie = (name, value, maxAgeSeconds) => {
  if (typeof document === 'undefined') return
  const parts = [`${name}=${encodeURIComponent(String(value))}`, 'path=/', `SameSite=Lax`]
  if (typeof maxAgeSeconds === 'number') parts.push(`max-age=${Number(maxAgeSeconds)}`)
  document.cookie = parts.join('; ')
}

let isRefreshing = false
let refreshPromise = null

const refreshAccessToken = async () => {
  if (isRefreshing) {
    console.info('[apiClient] Token refresh already in progress, returning pending promise')
    return refreshPromise
  }
  isRefreshing = true
  refreshPromise = (async () => {
    try {
      const id = getCookie('userId') || getCookie('id') || ''
      const refreshToken = getCookie('RefreshToken') || getCookie('refreshToken') || ''
      if (!id || !refreshToken) throw new Error('Missing id or refresh token')
      const url = `${apiClient.defaults.baseURL || ''}/auth/public/access/token`
      console.info('[apiClient] Refreshing access token', { url, id, time: new Date().toISOString() })
      let res
      try {
        res = await axios.post(url, { id, token: refreshToken })
        console.info('[apiClient] Token refresh response', res?.data)
      } catch (err) {
        console.error('[apiClient] Token refresh failed', err?.response?.data || err?.message || err)
        throw err
      }

      const token = res?.data?.token || res?.data?.accessToken || ''
      const expireAt = res?.data?.expireAt || res?.data?.expiresAt || res?.data?.timestamp || ''

      if (token) {
        setCookie('AccessToken', token, 7 * 24 * 3600)
        try { localStorage.setItem('AccessToken', String(token)) } catch {}
        console.info('[apiClient] New AccessToken saved (first 8 chars):', String(token).slice(0, 8) + '...')
      }
      if (expireAt) {
        setCookie('TimeStampAccessToken', String(expireAt), 7 * 24 * 3600)
        console.info('[apiClient] TimeStampAccessToken set to', expireAt)
      }
      return token
    } finally {
      isRefreshing = false
      refreshPromise = null
    }
  })()
  return refreshPromise
}

// Request interceptor: refresh token if access timestamp expired and attach Authorization header
apiClient.interceptors.request.use(async (config) => {
  try {
    const ts = Number(getCookie('TimeStampAccessToken') || 0)
    if (ts && Date.now() >= ts) {
      // Attempt refresh
      try {
        await refreshAccessToken()
      } catch (e) {
        // Ignore - let request proceed; backend will respond 401 if still unauthorized
      }
    }

    const access = getCookie('AccessToken') || localStorage.getItem('AccessToken') || ''
    const token = access.trim()
    if (token && !config.headers?.Authorization) {
      config.headers = config.headers || {}
      config.headers.Authorization = token.toLowerCase().startsWith('bearer ') ? token : `Bearer ${token}`
    }
  } catch (e) {
    // silence.
  }
  return config
}, (error) => Promise.reject(error))

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error?.code === 'ECONNABORTED') {
      return Promise.reject(new Error('Request timed out. Please retry.'))
    }
    if (!navigator.onLine) {
      return Promise.reject(new Error('You appear to be offline. Reconnect and try again.'))
    }
    return Promise.reject(error)
  },
)

export default apiClient
