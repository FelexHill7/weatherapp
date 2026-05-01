import React, { useState } from 'react'

export default function VideoSection({ videoData }) {
  const [playing, setPlaying] = useState(null)

  if (!videoData) return null

  const { videos, youtubeConfigured } = videoData

  if (!youtubeConfigured) {
    return (
      <div style={{ background: '#fff', borderRadius: '12px', padding: '1.25rem', boxShadow: '0 4px 6px -1px rgb(0 0 0/0.1)' }}>
        <h2 style={{ fontSize: '1rem', fontWeight: 600, color: '#1e293b', marginBottom: '0.5rem' }}>
          🎬 Location Videos
        </h2>
        <div style={{ background: '#fef9c3', border: '1px solid #fde68a', borderRadius: '8px', padding: '0.75rem', fontSize: '0.85rem', color: '#92400e' }}>
          ℹ️ Add a <code>YOUTUBE_API_KEY</code> environment variable to enable YouTube videos for searched locations.
        </div>
      </div>
    )
  }

  if (!videos || videos.length === 0) return null

  return (
    <div style={{ background: '#fff', borderRadius: '12px', padding: '1.25rem', boxShadow: '0 4px 6px -1px rgb(0 0 0/0.1)' }}>
      <h2 style={{ fontSize: '1rem', fontWeight: 600, color: '#1e293b', marginBottom: '1rem' }}>
        🎬 Location Videos
      </h2>
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '1rem' }}>
        {videos.map(v => (
          <div key={v.videoId} style={{ borderRadius: '10px', overflow: 'hidden', border: '1px solid #e2e8f0' }}>
            {playing === v.videoId ? (
              <iframe
                src={`${v.embedUrl}?autoplay=1`}
                width="100%"
                height="180"
                allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                allowFullScreen
                style={{ display: 'block', border: 'none' }}
                title={v.title}
              />
            ) : (
              <div
                style={{ position: 'relative', cursor: 'pointer' }}
                onClick={() => setPlaying(v.videoId)}
                role="button"
                tabIndex={0}
                onKeyDown={e => e.key === 'Enter' && setPlaying(v.videoId)}
                aria-label={`Play ${v.title}`}
              >
                <img src={v.thumbnailUrl} alt={v.title} style={{ width: '100%', height: '180px', objectFit: 'cover', display: 'block' }} />
                <div style={{
                  position: 'absolute', inset: 0, display: 'flex', alignItems: 'center', justifyContent: 'center',
                  background: 'rgba(0,0,0,0.3)',
                }}>
                  <span style={{ fontSize: '3rem' }}>▶️</span>
                </div>
              </div>
            )}
            <div style={{ padding: '0.6rem 0.75rem' }}>
              <p style={{ fontSize: '0.8rem', fontWeight: 600, color: '#1e293b', lineClamp: 2 }}>
                {v.title}
              </p>
              <p style={{ fontSize: '0.7rem', color: '#94a3b8', marginTop: '0.15rem' }}>{v.channelTitle}</p>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
