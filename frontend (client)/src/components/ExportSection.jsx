import React, { useState } from 'react'
import { weatherApi } from '../services/weatherApi'
import { downloadBlob } from '../utils/weatherUtils'

const FORMATS = [
  { key: 'json', label: 'JSON', icon: '{ }', color: '#f59e0b' },
  { key: 'csv', label: 'CSV', icon: '⊞', color: '#22c55e' },
  { key: 'xml', label: 'XML', icon: '</>', color: '#0ea5e9' },
  { key: 'pdf', label: 'PDF', icon: '📄', color: '#ef4444' },
  { key: 'markdown', label: 'Markdown', icon: '# ', color: '#8b5cf6' },
]

export default function ExportSection({ recordCount }) {
  const [loading, setLoading] = useState(null)
  const [error, setError] = useState('')

  async function handleExport(format) {
    setError('')
    setLoading(format)
    try {
      const response = await weatherApi.exportData(format)
      const ext = format === 'markdown' ? 'md' : format
      downloadBlob(response, `weather-records.${ext}`)
    } catch (err) {
      setError(err.message || 'Export failed')
    } finally {
      setLoading(null)
    }
  }

  return (
    <div style={{ background: '#fff', borderRadius: '12px', padding: '1.25rem', boxShadow: '0 4px 6px -1px rgb(0 0 0/0.1)' }}>
      <h2 style={{ fontSize: '1rem', fontWeight: 600, color: '#1e293b', marginBottom: '0.25rem' }}>
        ⬇️ Export Records
      </h2>
      <p style={{ fontSize: '0.8rem', color: '#64748b', marginBottom: '1rem' }}>
        Download all {recordCount > 0 ? recordCount : ''} saved records in your preferred format.
      </p>

      {error && (
        <p style={{ color: '#ef4444', fontSize: '0.8rem', marginBottom: '0.75rem', background: '#fef2f2', padding: '0.5rem', borderRadius: '6px' }}>
          ⚠️ {error}
        </p>
      )}

      <div style={{ display: 'flex', gap: '0.6rem', flexWrap: 'wrap' }}>
        {FORMATS.map(fmt => (
          <button
            key={fmt.key}
            onClick={() => handleExport(fmt.key)}
            disabled={!!loading || recordCount === 0}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '0.4rem',
              padding: '0.5rem 1rem',
              background: loading === fmt.key ? '#f1f5f9' : '#f8fafc',
              border: `2px solid ${fmt.color}`,
              color: fmt.color,
              borderRadius: '8px',
              fontWeight: 600,
              fontSize: '0.85rem',
              opacity: recordCount === 0 ? 0.5 : 1,
            }}
          >
            <span>{fmt.icon}</span>
            {loading === fmt.key ? 'Exporting…' : fmt.label}
          </button>
        ))}
      </div>

      {recordCount === 0 && (
        <p style={{ fontSize: '0.75rem', color: '#94a3b8', marginTop: '0.5rem' }}>
          Save records first using the form above.
        </p>
      )}
    </div>
  )
}
