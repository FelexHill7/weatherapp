import React, { useState } from 'react'
import { formatDateTime, today, daysAgo } from '../utils/weatherUtils'

export default function WeatherHistory({ records, onDelete, onEdit, loading }) {
  const [editId, setEditId] = useState(null)
  const [editForm, setEditForm] = useState({ location: '', startDate: '', endDate: '' })
  const [expanded, setExpanded] = useState(null)

  function startEdit(rec) {
    setEditId(rec.id)
    setEditForm({
      location: rec.location,
      startDate: rec.startDate,
      endDate: rec.endDate,
    })
  }

  function cancelEdit() {
    setEditId(null)
  }

  function submitEdit(e) {
    e.preventDefault()
    if (!editForm.location.trim()) return
    if (editForm.startDate > editForm.endDate) return
    onEdit(editId, editForm.location, editForm.startDate, editForm.endDate)
    setEditId(null)
  }

  if (!records || records.length === 0) {
    return (
      <div style={{ background: '#fff', borderRadius: '12px', padding: '1.5rem', boxShadow: '0 4px 6px -1px rgb(0 0 0/0.1)', textAlign: 'center' }}>
        <h2 style={{ fontSize: '1rem', fontWeight: 600, color: '#1e293b', marginBottom: '0.5rem' }}>📂 Saved Records</h2>
        <p style={{ color: '#94a3b8', fontSize: '0.85rem' }}>No records yet. Use the form above to save weather data.</p>
      </div>
    )
  }

  return (
    <div style={{ background: '#fff', borderRadius: '12px', padding: '1.25rem', boxShadow: '0 4px 6px -1px rgb(0 0 0/0.1)' }}>
      <h2 style={{ fontSize: '1rem', fontWeight: 600, color: '#1e293b', marginBottom: '1rem' }}>
        📂 Saved Records ({records.length})
      </h2>

      <div style={{ overflowX: 'auto' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '0.85rem' }}>
          <thead>
            <tr style={{ background: '#f8fafc', borderBottom: '2px solid #e2e8f0' }}>
              {['ID', 'Location', 'Start', 'End', 'Avg °C', 'Description', 'Saved', 'Actions'].map(h => (
                <th key={h} style={{ padding: '0.6rem 0.75rem', textAlign: 'left', fontWeight: 600, color: '#475569', whiteSpace: 'nowrap' }}>{h}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {records.map(rec => (
              <React.Fragment key={rec.id}>
                <tr style={{ borderBottom: '1px solid #f1f5f9', transition: 'background 0.1s' }}
                    onMouseEnter={e => e.currentTarget.style.background = '#f8fafc'}
                    onMouseLeave={e => e.currentTarget.style.background = ''}>
                  <td style={td}>{rec.id}</td>
                  <td style={td}>
                    <div style={{ fontWeight: 500 }}>{rec.weatherEmoji} {rec.locationName || rec.location}</div>
                    {rec.locationName && rec.locationName !== rec.location && (
                      <div style={{ fontSize: '0.75rem', color: '#94a3b8' }}>({rec.location})</div>
                    )}
                  </td>
                  <td style={td}>{rec.startDate}</td>
                  <td style={td}>{rec.endDate}</td>
                  <td style={td}>
                    <span style={{ fontWeight: 600 }}>{rec.temperature}°C</span>
                    <br/>
                    <span style={{ fontSize: '0.75rem', color: '#94a3b8' }}>
                      ↑{rec.tempMax}° ↓{rec.tempMin}°
                    </span>
                  </td>
                  <td style={td}>{rec.description}</td>
                  <td style={{ ...td, whiteSpace: 'nowrap', fontSize: '0.75rem', color: '#94a3b8' }}>{formatDateTime(rec.createdAt)}</td>
                  <td style={td}>
                    <div style={{ display: 'flex', gap: '0.4rem', flexWrap: 'wrap' }}>
                      <button
                        onClick={() => setExpanded(expanded === rec.id ? null : rec.id)}
                        style={btnStyle('#f0f9ff', '#0ea5e9')}
                      >
                        {expanded === rec.id ? '▲' : '▼'} Details
                      </button>
                      <button onClick={() => startEdit(rec)} style={btnStyle('#f0fdf4', '#16a34a')}>
                        ✏️ Edit
                      </button>
                      <button
                        onClick={() => { if (window.confirm(`Delete record #${rec.id}?`)) onDelete(rec.id) }}
                        style={btnStyle('#fef2f2', '#dc2626')}
                        disabled={loading}
                      >
                        🗑️ Del
                      </button>
                    </div>
                  </td>
                </tr>

                {editId === rec.id && (
                  <tr style={{ background: '#f0f9ff' }}>
                    <td colSpan={8} style={{ padding: '0.75rem 1rem' }}>
                      <form onSubmit={submitEdit} style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap', alignItems: 'flex-end' }}>
                        <div>
                          <label style={{ fontSize: '0.75rem', fontWeight: 600, display: 'block', marginBottom: '0.2rem' }}>Location</label>
                          <input
                            type="text"
                            value={editForm.location}
                            onChange={e => setEditForm(f => ({ ...f, location: e.target.value }))}
                            style={{ ...inputS, width: '180px' }}
                            required
                          />
                        </div>
                        <div>
                          <label style={{ fontSize: '0.75rem', fontWeight: 600, display: 'block', marginBottom: '0.2rem' }}>Start</label>
                          <input type="date" value={editForm.startDate}
                            onChange={e => setEditForm(f => ({ ...f, startDate: e.target.value }))}
                            style={inputS} required />
                        </div>
                        <div>
                          <label style={{ fontSize: '0.75rem', fontWeight: 600, display: 'block', marginBottom: '0.2rem' }}>End</label>
                          <input type="date" value={editForm.endDate}
                            onChange={e => setEditForm(f => ({ ...f, endDate: e.target.value }))}
                            min={editForm.startDate} style={inputS} required />
                        </div>
                        <button type="submit" disabled={loading} style={{ ...btnStyle('#2563eb', '#fff'), padding: '0.45rem 0.9rem', fontWeight: 600 }}>
                          {loading ? '⏳' : '✅ Save'}
                        </button>
                        <button type="button" onClick={cancelEdit} style={btnStyle('#f1f5f9', '#475569')}>
                          ✕ Cancel
                        </button>
                      </form>
                    </td>
                  </tr>
                )}

                {expanded === rec.id && rec.dailyData && rec.dailyData.length > 0 && (
                  <tr style={{ background: '#f8fafc' }}>
                    <td colSpan={8} style={{ padding: '0.75rem 1rem' }}>
                      <p style={{ fontSize: '0.8rem', fontWeight: 600, marginBottom: '0.5rem', color: '#475569' }}>Daily Breakdown:</p>
                      <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
                        {rec.dailyData.map(d => (
                          <div key={d.date} style={{
                            background: '#fff', border: '1px solid #e2e8f0', borderRadius: '8px',
                            padding: '0.5rem 0.75rem', textAlign: 'center', minWidth: '80px'
                          }}>
                            <p style={{ fontSize: '0.7rem', color: '#64748b' }}>{d.date}</p>
                            <p style={{ fontSize: '1.2rem' }}>{getEmoji(d.weatherCode)}</p>
                            <p style={{ fontSize: '0.8rem', fontWeight: 600 }}>↑{d.tempMax}°</p>
                            <p style={{ fontSize: '0.75rem', color: '#94a3b8' }}>↓{d.tempMin}°</p>
                          </div>
                        ))}
                      </div>
                    </td>
                  </tr>
                )}
              </React.Fragment>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}

function getEmoji(code) {
  if (code === 0) return '☀️'
  if (code <= 3) return ['🌤️','⛅','☁️'][code - 1] || '☁️'
  if (code <= 48) return '🌫️'
  if (code <= 67) return '🌧️'
  if (code <= 77) return '❄️'
  if (code <= 82) return '🌦️'
  if (code <= 99) return '⛈️'
  return '🌡️'
}

const td = { padding: '0.6rem 0.75rem', verticalAlign: 'top' }

function btnStyle(bg, color) {
  return {
    background: bg,
    color: color,
    border: `1px solid ${color}`,
    borderRadius: '6px',
    padding: '0.3rem 0.6rem',
    fontSize: '0.75rem',
    fontWeight: 500,
    whiteSpace: 'nowrap',
  }
}

const inputS = {
  padding: '0.4rem 0.6rem',
  border: '1px solid #e2e8f0',
  borderRadius: '6px',
  fontSize: '0.85rem',
}
