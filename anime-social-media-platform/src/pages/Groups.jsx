import { useEffect, useMemo, useState } from 'react'
import { AnimatePresence, motion } from 'framer-motion'
import AlertBanner from '../components/AlertBanner.jsx'
import accentOptions from '../theme/accentOptions.js'
import { getSavedAccentKey } from '../theme/accentStorage.js'
import NavigationBar from '../components/NavigationBar.jsx'
import { Users, Plus, Calendar, X } from 'lucide-react'

const seedGroups = [
  { id: 'g1', name: 'Shonen Shapers', members: 1240, activity: 'Daily', image: '' },
  { id: 'g2', name: 'Studio Ghibli Fans', members: 842, activity: 'Weekly', image: '' },
  { id: 'g3', name: 'Retro Otaku', members: 312, activity: 'Monthly', image: '' },
  { id: 'g4', name: 'Cosplay Creators', members: 540, activity: 'Weekly', image: '' },
  { id: 'g5', name: 'Silent Protagonists', members: 78, activity: 'Occasional', image: '' },
]

function Groups() {
  const [accentKey] = useState(() => getSavedAccentKey('crimson-night'))
  const accent = useMemo(() => accentOptions.find((opt) => opt.key === accentKey)?.colors || accentOptions[0].colors, [accentKey])
  const [groups, setGroups] = useState(seedGroups)
  const [showCreateModal, setShowCreateModal] = useState(false)

  // Create Group form state
  const [groupName, setGroupName] = useState('')
  const [groupBio, setGroupBio] = useState('')
  const [iconFile, setIconFile] = useState(null)
  const [bgFile, setBgFile] = useState(null)
  const [iconPreview, setIconPreview] = useState(null)
  const [bgPreview, setBgPreview] = useState(null)
  const [fileError, setFileError] = useState('')
  const [apiStatus, setApiStatus] = useState('')
  const [apiMessage, setApiMessage] = useState('')
  const [isSubmittingCreate, setIsSubmittingCreate] = useState(false)
  const maxFileSize = 5 * 1024 * 1024

  const getCookie = (name) => {
    if (typeof document === 'undefined') return ''
    const match = document.cookie.match(new RegExp(`(?:^|; )${name}=([^;]*)`))
    return match ? decodeURIComponent(match[1]) : ''
  }

  const getBearerToken = () => {
    const raw = getCookie('AccessToken') || getCookie('accessToken') || (typeof localStorage !== 'undefined' ? localStorage.getItem('AccessToken') || localStorage.getItem('accessToken') : '') || ''
    const trimmed = raw.trim()
    if (!trimmed) return ''
    // If the stored value already includes a Bearer prefix (in any case), strip it and normalize
    const token = trimmed.replace(/^\s*bearer\s+/i, '').trim()
    return token ? `Bearer ${token}` : ''
  }

  useEffect(() => {
    document.title = 'Groups - ThatOtakuNetwork'
  }, [])

  return (
    <div
      className="relative min-h-screen overflow-visible text-slate-50"
      style={{ backgroundImage: `linear-gradient(135deg, ${accent.start}, ${accent.mid}, ${accent.end})` }}
    >
      <div
        className="pointer-events-none absolute inset-0"
        style={{
          backgroundImage: `radial-gradient(circle at 45% 20%, ${accent.glow}, transparent 38%), radial-gradient(circle at 78% 30%, rgba(255,255,255,0.06), transparent 30%), radial-gradient(circle at 55% 78%, rgba(255,255,255,0.04), transparent 33%)`,
          mixBlendMode: 'screen',
        }}
      />

      <div className="relative z-10 px-4 pb-20 pt-14 sm:px-8 lg:px-12 xl:px-16">
        <div className="md:grid md:grid-cols-[20rem_1fr] md:gap-8">
          <div className="hidden md:block">
            <NavigationBar accent={accent} variant="inline" />
          </div>
          <div className="min-w-0">
            <header className="flex flex-wrap items-center justify-between gap-4">
              <div>
                <p className="text-xs uppercase tracking-[0.25em] text-slate-300/80">Communities</p>
                <h1 className="mt-2 text-3xl font-semibold text-white">Groups</h1>
                <p className="mt-2 text-sm text-slate-200/85 max-w-2xl">Join active groups to host watch parties, meetups, and themed discussions.</p>
              </div>
              <div>
                <button onClick={() => setShowCreateModal(true)} className="rounded-full border border-white/15 bg-white/10 px-4 py-2 text-sm font-semibold text-white shadow transition hover:bg-white/20 flex items-center gap-2"><Plus className="h-4 w-4" /> Create group</button>
              </div>
            </header>

            <div className="mt-8 grid gap-6">
              {groups.map((g) => (
                <div key={g.id} className="rounded-3xl border border-white/10 bg-white/5 p-6 shadow-[0_18px_60px_rgba(0,0,0,0.35)] backdrop-blur-2xl">
                  <div className="flex items-center justify-between gap-4">
                    <div className="flex items-center gap-4">
                      <div className="h-14 w-14 rounded-xl bg-white/8 flex items-center justify-center text-xl font-semibold text-white">{g.name.slice(0,2)}</div>
                      <div>
                        <h3 className="text-lg font-semibold text-white">{g.name}</h3>
                        <p className="text-sm text-slate-300/80">{g.members} members · {g.activity}</p>
                      </div>
                    </div>
                    <div className="flex items-center gap-3">
                      <button className="rounded-full border border-white/10 bg-white/5 px-3 py-1.5 text-sm font-semibold text-slate-200/85 hover:text-white">Join</button>
                    </div>
                  </div>
                </div>
              ))}
            </div>

            {/* Create Group Modal */}
            <AnimatePresence>
              {showCreateModal && (
                <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} className="fixed inset-0 z-[120] flex items-center justify-center bg-black/80 backdrop-blur-md p-4" onClick={() => setShowCreateModal(false)}>
                  <motion.div initial={{ opacity: 0, scale: 0.95, y: 18 }} animate={{ opacity: 1, scale: 1, y: 0 }} exit={{ opacity: 0, scale: 0.95, y: 18 }} transition={{ type: 'spring', stiffness: 280, damping: 24 }} className="relative w-full max-w-3xl h-auto overflow-hidden rounded-3xl border backdrop-blur-3xl flex flex-col" style={{ borderColor: 'rgba(255,255,255,0.08)', backgroundColor: accent.surface, boxShadow: `0 40px 100px rgba(0,0,0,0.6), 0 0 50px ${accent.glow}` }} onClick={(e) => e.stopPropagation()}>
                    <div className="pointer-events-none absolute -left-24 -top-24 h-72 w-72 rounded-full bg-purple-500/20 blur-3xl" />
                    <div className="pointer-events-none absolute -bottom-24 -right-24 h-80 w-80 rounded-full bg-indigo-500/25 blur-3xl" />

                    <div className="relative p-6 border-b border-white/10">
                      <div className="flex items-center justify-between mb-4">
                        <div>
                          <h2 className="text-2xl font-semibold text-white">Create a Group</h2>
                          <p className="text-sm text-slate-300/80 mt-1">Set a name, bio, icon, and background for your group.</p>
                        </div>
                        <button onClick={() => setShowCreateModal(false)} className="rounded-full p-2 text-slate-300 hover:bg-white/10 hover:text-white transition"><X className="h-6 w-6" /></button>
                      </div>
                    </div>

                    <div className="relative p-6">
                      {apiMessage && <div className="mb-4"><AlertBanner status={apiStatus === 'success' ? 'success' : 'error'} message={apiMessage} /></div>}

                      <div className="space-y-4">
                        <div>
                          <label className="text-sm font-medium text-slate-200 mb-2 block">Group Name</label>
                          <input value={groupName} onChange={(e) => setGroupName(e.target.value)} className="w-full rounded-xl border border-white/15 bg-black/30 px-4 py-3 text-base text-slate-200 placeholder:text-slate-400 focus:border-white/30 focus:outline-none focus:ring-2 focus:ring-white/20" placeholder="Group name" />
                        </div>

                        <div>
                          <label className="text-sm font-medium text-slate-200 mb-2 block">Group Bio</label>
                          <textarea value={groupBio} onChange={(e) => setGroupBio(e.target.value)} className="w-full rounded-xl border border-white/15 bg-black/30 px-4 py-3 text-base text-slate-200 placeholder:text-slate-400 focus:border-white/30 focus:outline-none focus:ring-2 focus:ring-white/20" rows={3} placeholder="Short description..." />
                        </div>

                        <div>
                          <label className="text-sm font-medium text-slate-200 mb-2 block">Group Icon</label>
                          <input type="file" accept="image/*" onChange={(e) => { const f = e.target.files?.[0] || null; if (f && f.size > maxFileSize) { setFileError('File exceeds 5 MB limit.'); return } setIconFile(f); if (f) { const reader = new FileReader(); reader.onload = () => setIconPreview(reader.result); reader.readAsDataURL(f) } }} className="block w-full text-sm text-slate-300 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-white/10 file:text-white hover:file:bg-white/20" />
                          {iconPreview && (<div className="flex items-center gap-3 mt-3"><img src={iconPreview} alt="Icon preview" className="h-16 w-16 rounded-xl object-cover border border-white/20" /><span className="text-sm text-slate-300">Image selected</span></div>)}
                        </div>

                        <div>
                          <label className="text-sm font-medium text-slate-200 mb-2 block">Group Background</label>
                          <input type="file" accept="image/*" onChange={(e) => { const f = e.target.files?.[0] || null; if (f && f.size > maxFileSize) { setFileError('File exceeds 5 MB limit.'); return } setBgFile(f); if (f) { const reader = new FileReader(); reader.onload = () => setBgPreview(reader.result); reader.readAsDataURL(f) } }} className="block w-full text-sm text-slate-300 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-white/10 file:text-white hover:file:bg-white/20" />
                          {bgPreview && (<div className="flex items-center gap-3 mt-3"><img src={bgPreview} alt="BG preview" className="h-16 w-16 rounded-xl object-cover border border-white/20" /><span className="text-sm text-slate-300">Image selected</span></div>)}
                        </div>

                        {fileError && <div className="mt-2"><AlertBanner status="error" message={fileError} /></div>}

                      </div>
                    </div>

                    <div className="relative p-6 border-t border-white/10 flex items-center justify-between gap-4">
                      <button onClick={() => setShowCreateModal(false)} className="rounded-xl border border-white/10 bg-white/5 px-5 py-2.5 text-sm font-semibold text-white transition hover:bg-white/10">Cancel</button>
                      <button onClick={async () => {
                        // client side validation
                        setFileError('')
                        setApiMessage('')
                        if (!groupName.trim() || !groupBio.trim() || !iconFile || !bgFile) { setApiMessage('All fields are required.'); setApiStatus('error'); return }
                        setIsSubmittingCreate(true)
                        try {
                          const leaderId = getCookie('userId') || getCookie('id') || ''
                          const dto = { groupName: String(groupName).trim(), groupBio: String(groupBio).trim(), leaderId }
                          const formData = new FormData()
                          formData.append('requestDto', new Blob([JSON.stringify(dto)], { type: 'application/json' }))
                          formData.append('profileImage', iconFile)
                          formData.append('bgImage', bgFile)

                          const token = getBearerToken()
                          const headers = token ? { Authorization: token } : undefined

                          const url = 'http://localhost:11111/api/profile/group/create'
                          // Log the exact URL being called and whether an Authorization header is present (token redacted)
                          console.log('POST', url, 'Authorization present:', !!headers?.Authorization)

                          const res = await fetch(url, { method: 'POST', body: formData, headers })
                          const body = await res.json().catch(() => ({}))
                          if (!res.ok) {
                            const parsed = (await import('../lib/parseApiError.js')).parseApiErrorBody(body)
                            throw new Error(parsed.message || 'Failed to create group')
                          }
                          const data = body
                          setApiMessage('Group created')
                          setApiStatus('success')
                          // add to list
                          setGroups((prev) => [{ id: data?.data?.id || `g${Date.now()}`, name: dto.groupName, members: 1, activity: 'Now', image: iconPreview }, ...prev])
                          setTimeout(() => setShowCreateModal(false), 800)
                        } catch (err) {
                          setApiMessage(err.message || 'Failed to create group')
                          setApiStatus('error')
                        } finally {
                          setIsSubmittingCreate(false)
                        }
                      }} className="rounded-xl px-5 py-2.5 text-sm font-semibold text-white transition" style={{ backgroundImage: `linear-gradient(90deg, ${accent.mid}, ${accent.strong})`, boxShadow: `0 8px 24px ${accent.glow}` }}>{isSubmittingCreate ? 'Creating...' : 'Create'}</button>
                    </div>
                  </motion.div>
                </motion.div>
              )}
            </AnimatePresence>
          </div>
        </div>
      </div>

      <NavigationBar accent={accent} variant="mobile" />
    </div>
  )
}

export default Groups
