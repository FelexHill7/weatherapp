import React, { useState } from 'react'
import { today, daysAgo } from '../utils/weatherUtils'

export default function DateRangeForm({ onSubmit, loading }) {
  const [location, setLocation] = useState('')
  const [startDate, setStartDate] = useState(daysAgo(7))
  const [endDate, setEndDate] = useState(today())
  const [error, setError] = useState('')

  function handleSubmit(e) {
    e.preventDefault()
    setError('')

    const loc = location.trim()
    if (!loc) { setError('Location is required'); return }
    if (!startDate) { setError('Start date is required'); return }
    if (!endDate) { setError('End date is required'); return }
    if (startDate > endDate) { setError('Start date must be before end date'); return }

    const start = new Date(startDate)
    const end = new Date(endDate)
    const diffDays = (end - start) / (1000 * 60 * 60 * 24)
    if (diffDays > 365) { setError('Date range cannot exceed 1 year'); return }

    onSubmit(loc, startDate, endDate)
  }

  return (
    <div style={{ background: '#fff', borderRadius: '12px', padding: '1.25rem', boxShadow: '0 4px 6px -1px rgb(0 0 0/0.1)', border: '2px solid #dbeafe' }}>
      <h2 style={{ fontSize: '1rem', fontWeight: 600, marginBottom: '0.25rem', color: '#1e293b' }}>
        📅 Save Historical / Forecast Data
      </h2>
      <p style={{ fontSize: '0.8rem', color: '#64748b', marginBottom: '1rem' }}>
        Query temperature for a location over a date range and save it to the database.
      </p>

      <form onSubmit={handleSubmit}>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '0.75rem', marginBottom: '0.75rem' }}>
          <div>
            <label style={labelStyle}>Location *</label>
            <input
              type="text"
              value={location}
              onChange={e => setLocation(e.target.value)}
              placeholder="e.g. Paris, 10001, Tokyo..."
              style={inputStyle}
              disabled={loading}
            />
          </div>
          <div>
            <label style={labelStyle}>Start Date *</label>
            <input
              type="date"
              value={startDate}
              onChange={e => setStartDate(e.target.value)}
              max={today()}
              style={inputStyle}
              disabled={loading}
            />
          </div>
          <div>
            <label style={labelStyle}>End Date *</label>
            <input
              type="date"
              value={endDate}
              onChange={e => setEndDate(e.target.value)}
              min={startDate}
              style={inputStyle}
              disabled={loading}
            />
          </div>
        </div>

        {error && (
          <p style={{ color: '#ef4444', fontSize: '0.8rem', marginBottom: '0.75rem', background: '#fef2f2', padding: '0.5rem 0.75rem', borderRadius: '6px' }}>
            ⚠️ {error}
          </p>
        )}

        <button
          type="submit"
          disabled={loading}
          style={{
            padding: '0.6rem 1.5rem',
            background: loading ? '#93c5fd' : '#2563eb',
            color: '#fff',
            borderRadius: '8px',
            fontWeight: 600,
            fontSize: '0.9rem',
          }}
        >
          {loading ? '⏳ Saving…' : '💾 Save to Database'}
        </button>
      </form>
    </div>
  )
}

const labelStyle = {
  display: 'block',
  fontSize: '0.8rem',
  fontWeight: 600,
  color: '#475569',
  marginBottom: '0.3rem',
}

const inputStyle = {
  width: '100%',
  padding: '0.55rem 0.75rem',
  border: '2px solid #e2e8f0',
  borderRadius: '8px',
  fontSize: '0.9rem',
}
